package usecase.createRoom

import cats.effect.IO
import repository.RoomsRepository
import usecase.createRoom.model.CreateRoomResult

case class CreateRoomUseCase(private val roomsRepository: RoomsRepository):

  def createRoom(roomNumber: Short): IO[CreateRoomResult] =
    roomsRepository.createRoom(roomNumber)
