package service

import cats.effect.IO
import model.{OperationConclusion, Room}
import repository.RoomsRepository

case class CreateRoomService(roomsRepository: RoomsRepository):

  def createRoom(roomNumber: Short): IO[OperationConclusion] =
    roomsRepository.createRoom(roomNumber)
