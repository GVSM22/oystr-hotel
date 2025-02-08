package repository

import cats.effect.IO
import model.Reservation
import repository.entity.ReservationEntity
import repository.mapper.ReservationMapper
import skunk.{Command, Fragment, Query, Session}
import skunk.codec.all.{int2, timestamp}
import skunk.data.Completion
import skunk.implicits.sql
import usecase.createReservation.model.CreateReservationResult

import java.time.LocalDateTime

trait ReservationsRepository:
  def findConflictingReservations(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime): IO[List[Reservation]]
  def createReservation(reservation: Reservation): IO[CreateReservationResult]
  
object ReservationsRepository:
  
  def apply(session: Session[IO]): ReservationsRepository = new ReservationsRepository:
    private lazy val `conflict with another reservation`: Fragment[LocalDateTime] =
      sql"$timestamp BETWEEN check_out_date AND check_in_date"
  
    private lazy val `conflict between check-in and another check-out`: Fragment[LocalDateTime] =
      sql"$timestamp BETWEEN check_out_date AND check_out_date + INTERVAL '4 hours'"
  
    private lazy val `conflict between check-out and another check-in`: Fragment[LocalDateTime] =
      sql"$timestamp BETWEEN check_in_date AND check_in_date - INTERVAL '4 hours'"
  
    override def findConflictingReservations(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime): IO[List[Reservation]] =
      val q: Query[(Short, LocalDateTime, LocalDateTime, LocalDateTime, LocalDateTime), ReservationEntity] =
        sql"""
            SELECT reservations.room_number, reservations.check_in_date, reservations.check_out_date, reservations.guest_name
            FROM reservations
            WHERE room_number = $int2
            AND ${`conflict with another reservation`}
            OR ${`conflict with another reservation`}
            OR ${`conflict between check-in and another check-out`}
            OR ${`conflict between check-out and another check-in`}
            LIMIT 1
      """.query(ReservationEntity.reservationDecoder)
      session
        .prepare(q)
        .flatMap(
          _.stream((roomNumber, checkInDate, checkOutDate, checkInDate, checkOutDate), 32)
            .compile
            .fold(List.empty)((list, res) => list :+ ReservationMapper.fromEntity(res))
        )
  
    override def createReservation(reservation: Reservation): IO[CreateReservationResult] =
      val command: Command[ReservationEntity] =
        sql"""INSERT INTO reservations (room_number, check_in_date, check_out_date, guest_name) VALUES ${ReservationEntity.reservationEncoder}""".command
      session.prepare(command)
        .flatMap(_.execute(ReservationMapper.toEntity(reservation)))
        .map(_ => CreateReservationResult.ReservationCreated)
