package service

import cats.data.EitherT
import cats.effect.IO
import exception.CustomError
import model.Reservation
import repository.{ReservationsRepository, RoomsRepository}
import skunk.data.Completion

case class CreateReservationService(roomsRepository: RoomsRepository, reservationsRepository: ReservationsRepository):
  def createReservation(reservation: Reservation): IO[Either[CustomError, Completion]] =
    EitherT(roomsRepository.findRoom(reservation.roomNumber).map(_.toRight(CustomError.InvalidRoom)))
      .flatMapF { room =>
        reservationsRepository.findConflict(room, reservation.checkInDate, reservation.checkOutDate)
          .map(
            _.map(_ => CustomError.ConflictingReservation)
              .toLeft(reservation)
          )
      }
      .semiflatMap(reservationsRepository.insertReservation)
      .value