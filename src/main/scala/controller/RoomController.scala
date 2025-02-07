package controller

import cats.effect.IO
import model.OperationConclusion
import org.http4s.Method.PUT
import org.http4s.dsl.io.{->, /, Created, DELETE, NoContent, Root}
import org.http4s.{HttpRoutes, Request, Response, Status}
import service.CreateRoomService

case class RoomController(createRoomService: CreateRoomService) extends Controller:

  override val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case PUT -> Root / "rooms" / ShortValue(roomNumber) => createRoom(roomNumber)
    case DELETE -> Root / "rooms" / ShortValue(roomNumber) => deleteRoom(roomNumber)
  }

  object ShortValue:
    def unapply(str: String): Option[Short] = str.toShortOption

  private def createRoom(roomNumber: Short): IO[Response[IO]] =
    for {
      status: Status <- createRoomService.createRoom(roomNumber).map {
        case OperationConclusion.Created => Created
        case OperationConclusion.AlreadyExist => NoContent
      }
    } yield Response[IO](status)
    
  private def deleteRoom(roomNumber: Short): IO[Response[IO]] = ???