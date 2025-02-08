package controller

import cats.effect.IO
import controller.mapper.ReservationMapper
import controller.request.ReservationRequest
import usecase.createReservation.model.CreateReservationError.{ConflictingReservation, InvalidRoom}
import org.http4s.Method.POST
import org.http4s.dsl.io.{->, /, Conflict, Created, NotFound, Root}
import org.http4s.{HttpRoutes, Request, Response}
import usecase.createReservation.CreateReservationUseCase
import usecase.createReservation.model.CreateReservationResult

case class ReservationController(createReservationService: CreateReservationUseCase) extends Controller:
  override val routes: HttpRoutes[IO] = HttpRoutes.of {
    case req @ POST -> Root / "reservation" => createReservation(req)
  }

  def createReservation(request: Request[IO]): IO[Response[IO]] =
    val reservationResult = for {
      body <- request.as[ReservationRequest]
      reservation = ReservationMapper.fromRequest(body)
      result <- createReservationService.createReservation(reservation)
    } yield result

    reservationResult.map {
      case Left(InvalidRoom) => Response[IO](NotFound)
      case Left(ConflictingReservation) => Response[IO](Conflict)
      case Right(CreateReservationResult.ReservationCreated) => Response[IO](Created)
    }.onError(IO.println)