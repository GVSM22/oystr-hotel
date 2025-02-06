package service

import cats.effect.IO
import model.Reservation
import repository.{ReservationsRepository, RoomsRepository}
import skunk.data.Completion

case class CreateReservationService(roomsRepository: RoomsRepository, reservationsRepository: ReservationsRepository):
  def createReservation(reservation: Reservation): IO[Completion] =
    for {
      room <- roomsRepository.findRoom(1)
      _ <- IO.println(s"quarto: $room")
      conflictReservation <- reservationsRepository.findConflict(reservation.checkInDate, reservation.checkOutDate)
      _ <- IO.println(s"reserva no mesmo horário: $conflictReservation")
      completion <- if (conflictReservation.isEmpty) reservationsRepository.insertReservation(reservation).flatTap(IO.println) else IO.raiseError(new Throwable("já está reservado!"))
    } yield completion