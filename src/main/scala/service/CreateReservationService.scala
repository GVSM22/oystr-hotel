package service

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.functor.toFunctorOps
import exception.CustomError
import model.Reservation
import repository.{ReservationsRepository, RoomsRepository}
import skunk.data.Completion

case class CreateReservationService(roomsRepository: RoomsRepository, reservationsRepository: ReservationsRepository):
  def createReservation(reservation: Reservation): IO[Either[CustomError, Completion]] =
    EitherT
      .apply(validateRoom(reservation))
      .flatMapF(validateReservation)
      .semiflatMap(reservationsRepository.insertReservation)
      .value
  
  private def validateRoom(reservation: Reservation): IO[Either[CustomError, Reservation]] =
    roomsRepository.findRoom(reservation.roomNumber).map {
      _.as(reservation)
        .toRight(CustomError.InvalidRoom)
    }

  private def validateReservation(reservation: Reservation): IO[Either[CustomError, Reservation]] =
    reservationsRepository.findConflict(reservation.roomNumber, reservation.checkInDate, reservation.checkOutDate)
      .map {
        case None => Right(reservation)
        case _ => Left(CustomError.ConflictingReservation)
      }