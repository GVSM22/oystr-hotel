package usecase.removeRoom

import cats.effect.IO
import repository.RoomsRepository
import repository.exception.PostgresException
import usecase.removeRoom.model.RemoveRoomError.`Can't remove a reserved room`
import usecase.removeRoom.model.{RemoveRoomError, RemoveRoomResult}

case class RemoveRoomUseCase(private val roomsRepository: RoomsRepository):
  
  def removeRoom(roomNumber: Short): IO[Either[RemoveRoomError, RemoveRoomResult]] =
    roomsRepository.deleteRoom(roomNumber)
      .map(Right.apply)
      .recover {
        case PostgresException.ForeignKeyConstraintViolation => Left(`Can't remove a reserved room`)
      }
