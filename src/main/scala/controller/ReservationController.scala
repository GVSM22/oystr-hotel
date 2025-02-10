package controller

import cats.effect.IO
import controller.mapper.ReservationMapper
import controller.request.ReservationRequest
import controller.response.OccupancyResponse
import usecase.createReservation.model.CreateReservationError.{ConflictingReservation, InvalidRoom}
import org.http4s.Method.{GET, POST}
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.dsl.io.{->, /, :?, Conflict, Created, NotFound, Root}
import org.http4s.{HttpRoutes, QueryParamDecoder, Request, Response}
import usecase.createReservation.CreateReservationUseCase
import usecase.createReservation.model.CreateReservationResult
import usecase.occupancy.OccupancyUseCase

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class ReservationController(createReservationUseCase: CreateReservationUseCase, occupancyUseCase: OccupancyUseCase) extends Controller:
  override val routes: HttpRoutes[IO] = HttpRoutes.of {
    case req @ POST -> Root / "reservation" => createReservation(req)
    case GET -> Root / "reservation" / "occupancy" :? LocalDateParamDecoderMatcher(date) => occupancyForDay(date)
  }

  given QueryParamDecoder[LocalDate] = QueryParamDecoder.localDate(DateTimeFormatter.ISO_LOCAL_DATE)
  private object LocalDateParamDecoderMatcher extends QueryParamDecoderMatcher[LocalDate]("date")

  private def createReservation(request: Request[IO]): IO[Response[IO]] =
    val reservationResult = for {
      body <- request.as[ReservationRequest]
      reservation = ReservationMapper.fromRequest(body)
      result <- createReservationUseCase.createReservation(reservation)
    } yield result

    reservationResult.map {
      case Left(InvalidRoom) => Response[IO](NotFound)
      case Left(ConflictingReservation) => Response[IO](Conflict)
      case Right(CreateReservationResult.ReservationCreated) => Response[IO](Created)
    }

  private def occupancyForDay(date: LocalDate): IO[Response[IO]] =
    occupancyUseCase.getOccupancyForDay(date)
      .map(OccupancyResponse.apply)
      .map(Response[IO]().withEntity)