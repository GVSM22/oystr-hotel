import cats.effect.*
import cats.effect.IOApp.Simple
import model.Reservation
import skunk.*
import natchez.Trace.Implicits.noop
import repository.{ReservationsRepository, RoomsRepository}
import service.CreateReservationService

import java.time.LocalDateTime

object Main extends Simple:

  private val sessionResource: Resource[IO, Session[IO]] =
    Session.single(
      host = "localhost",
      port = 5432,
      user = "dbuser",
      database = "oystr-hotel",
      password = Some("dbpassword")
    )

  private val reservation = Reservation(1, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusDays(1L), "jersin")

  override def run: IO[Unit] =
    sessionResource.use { session =>
      val roomsRepository = RoomsRepository(session)
      val reservationsRepository = ReservationsRepository(session)
      val createReservationService = CreateReservationService(roomsRepository, reservationsRepository)

      createReservationService.createReservation(reservation)
        .flatMap {
          case Left(value) => IO.println(s"erro! $value")
          case Right(value) => IO.println(s"sucesso! $value")
        }
    }
