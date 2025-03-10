package controller

import cats.effect.IO
import org.http4s.Method.PUT
import org.http4s.dsl.io.{->, /, Conflict, Created, DELETE, NoContent, Root}
import org.http4s.{HttpRoutes, Request, Response}
import usecase.createRoom.CreateRoomUseCase
import usecase.createRoom.model.CreateRoomResult
import usecase.removeRoom.RemoveRoomUseCase
import usecase.removeRoom.model.{RemoveRoomError, RemoveRoomResult}

case class RoomController(createRoomUseCase: CreateRoomUseCase, removeRoomUseCase: RemoveRoomUseCase) extends Controller:

  override val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case PUT -> Root / "rooms" / ShortValue(roomNumber) => createRoom(roomNumber)
    case DELETE -> Root / "rooms" / ShortValue(roomNumber) => deleteRoom(roomNumber)
  }

  private object ShortValue:
    def unapply(str: String): Option[Short] = str.toShortOption

  private def createRoom(roomNumber: Short): IO[Response[IO]] =
    createRoomUseCase.createRoom(roomNumber).map {
      case CreateRoomResult.Created => Created
      case CreateRoomResult.AlreadyExist => NoContent
    }.map(Response[IO].apply(_))

  private def deleteRoom(roomNumber: Short): IO[Response[IO]] =
    removeRoomUseCase.removeRoom(roomNumber).map {
      case Left(RemoveRoomError.`Can't remove a reserved room`) => Conflict
      case Right(RemoveRoomResult.Success) => NoContent
    }.map(Response[IO].apply(_))
