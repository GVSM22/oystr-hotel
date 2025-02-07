import cats.effect.*
import cats.effect.IOApp.Simple
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s.{ipv4, port}
import controller.{ReservationController, RoomController}
import natchez.Trace.Implicits.noop
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.noop.NoOpFactory
import repository.{ReservationsRepository, RoomsRepository}
import service.{CreateReservationService, CreateRoomService}
import skunk.*

object Main extends Simple:

  given LoggerFactory[IO] = NoOpFactory[IO]

  private val sessionResource: Resource[IO, Session[IO]] =
    Session.single(
      host = "localhost",
      port = 5432,
      user = "dbuser",
      database = "oystr-hotel",
      password = Some("dbpassword")
    )

  override def run: IO[Unit] =
    sessionResource.flatMap { session =>
      val roomsRepository = RoomsRepository(session)
      val reservationsRepository = ReservationsRepository(session)
      val createReservationService = CreateReservationService(roomsRepository, reservationsRepository)
      val createRoomService = CreateRoomService(roomsRepository)
      val roomController = RoomController(createRoomService)
      val reservationController = ReservationController()
      
      val httpApp = roomController.routes <+> reservationController.routes
      
      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp.orNotFound)
        .build
    }.useForever
