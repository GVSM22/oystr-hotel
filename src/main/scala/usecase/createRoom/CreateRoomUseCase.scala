package usecase.createRoom

import cats.effect.IO
import _root_.model.Room
import repository.RoomsRepository
import usecase.createRoom.model.CreateRoomResult

case class CreateRoomUseCase(roomsRepository: RoomsRepository):

  def createRoom(roomNumber: Short): IO[CreateRoomResult] =
    roomsRepository.createRoom(roomNumber)
