package usecase.createReservation

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.functor.toFunctorOps
import model.{CreateReservationError, CreateReservationResult}
import _root_.model.Reservation
import repository.{ReservationsRepository, RoomsRepository}

case class CreateReservationUseCase(roomsRepository: RoomsRepository, reservationsRepository: ReservationsRepository):
  def createReservation(reservation: Reservation): IO[Either[CreateReservationError, CreateReservationResult]] =
    EitherT
      .apply(validateRoom(reservation))
      .flatMapF(validateReservation)
      .semiflatMap(reservationsRepository.createReservation)
      .value
  
  private def validateRoom(reservation: Reservation): IO[Either[CreateReservationError, Reservation]] =
    roomsRepository.findRoom(reservation.roomNumber).map {
      _.as(reservation)
        .toRight(CreateReservationError.InvalidRoom)
    }

  private def validateReservation(reservation: Reservation): IO[Either[CreateReservationError, Reservation]] =
    reservationsRepository.findConflictingReservations(reservation.roomNumber, reservation.checkInDate, reservation.checkOutDate)
      .map {
        case Nil => Right(reservation)
        case _ => Left(CreateReservationError.ConflictingReservation)
      }
