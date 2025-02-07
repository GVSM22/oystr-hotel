package repository

import cats.effect.IO
import model.Reservation
import skunk.{Command, Fragment, Query, Session}
import skunk.codec.all.{int2, timestamp}
import skunk.data.Completion
import skunk.implicits.sql

import java.time.LocalDateTime

trait ReservationsRepository:
  def findConflict(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime): IO[Option[Reservation]]
  def insertReservation(reservation: Reservation): IO[Completion]
  
object ReservationsRepository:
  
  def apply(session: Session[IO]): ReservationsRepository = new ReservationsRepository:
    private lazy val `conflict with another reservation`: Fragment[LocalDateTime] =
      sql"$timestamp BETWEEN check_out_date AND check_in_date"
  
    private lazy val `conflict between check-in and another check-out`: Fragment[LocalDateTime] =
      sql"$timestamp BETWEEN check_out_date AND check_out_date + INTERVAL '4 hours'"
  
    private lazy val `conflict between check-out and another check-in`: Fragment[LocalDateTime] =
      sql"$timestamp BETWEEN check_in_date AND check_in_date - INTERVAL '4 hours'"
  
    override def findConflict(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime): IO[Option[Reservation]] =
      val q: Query[(Short, LocalDateTime, LocalDateTime, LocalDateTime, LocalDateTime), Reservation] =
        sql"""
            SELECT reservations.room_number, reservations.check_in_date, reservations.check_out_date, reservations.guest_name
            FROM reservations
            WHERE room_number = $int2
            AND ${`conflict with another reservation`}
            OR ${`conflict with another reservation`}
            OR ${`conflict between check-in and another check-out`}
            OR ${`conflict between check-out and another check-in`}
            LIMIT 1
      """.query(Reservation.reservationDecoder)
      session
        .prepare(q)
        .flatMap(
          _.option((roomNumber, checkInDate, checkOutDate, checkInDate, checkOutDate))
        )
  
    override def insertReservation(reservation: Reservation): IO[Completion] =
      val command: Command[Reservation] =
        sql"""INSERT INTO reservations (room_number, check_in_date, check_out_date, guest_name) VALUES ${Reservation.reservationEncoder}""".command
      session.prepare(command)
        .flatMap(_.execute(reservation))
