package usecase

import cats.effect.IO
import model.{Reservation, Room}
import org.scalamock.stubs.{CatsEffectStubs, Stub}
import repository.{ReservationsRepository, RoomsRepository}
import usecase.createReservation.CreateReservationUseCase
import usecase.createReservation.model.CreateReservationResult
import weaver.SimpleIOSuite

import java.time.LocalDateTime
import scala.annotation.experimental

@experimental
object CreateReservationTest extends SimpleIOSuite, CatsEffectStubs:

  class TestResource(
                      val createReservationUseCase: CreateReservationUseCase,
                      val roomsRepositoryMock: Stub[RoomsRepository],
                      val reservationsRepositoryMock: Stub[ReservationsRepository]
                    )

  object TestResource:
    def apply(): TestResource =
      val roomsMock = stub[RoomsRepository]
      val reservationsMock = stub[ReservationsRepository]
      new TestResource(CreateReservationUseCase(roomsMock, reservationsMock), roomsMock, reservationsMock)

  test("should not allow a reservation on a non-existent room") {
    val testResource = TestResource()
    val roomNumber: Short = 1
    val reservation = Reservation(roomNumber, LocalDateTime.now(), LocalDateTime.now(), "name")

    testResource.roomsRepositoryMock.findRoom.returns:
      case _ => IO.none

    testResource.createReservationUseCase.createReservation(reservation).map {
      case Left(value) => expect(testResource.roomsRepositoryMock.findRoom.times == 1)
      case Right(value) => failure("a non-existent room was reserved!")
    }
  }

  test("should not allow duplicate reservation") {
    val testResource = TestResource()
    val roomNumber: Short = 1
    val reservation = Reservation(roomNumber, LocalDateTime.now(), LocalDateTime.now(), "name")

    testResource.roomsRepositoryMock.findRoom.returns:
      case _ => IO.some(Room(roomNumber))

    testResource.reservationsRepositoryMock.findConflictingReservations.returns:
      case _ => IO.pure(List(reservation))

    testResource.createReservationUseCase.createReservation(reservation).map {
      case Left(value) => expect.all(
        testResource.roomsRepositoryMock.findRoom.times == 1,
        testResource.reservationsRepositoryMock.findConflictingReservations.times == 1
      )
      case Right(value) => failure("duplicated reservation was created!")
    }
  }

  test("should create a reservation from a valid request") {
    val testResource = TestResource()
    val roomNumber: Short = 1
    val reservation = Reservation(roomNumber, LocalDateTime.now(), LocalDateTime.now(), "name")

    testResource.roomsRepositoryMock.findRoom.returns:
      case _ => IO.some(Room(roomNumber))

    testResource.reservationsRepositoryMock.findConflictingReservations.returns:
      case _ => IO.pure(List.empty)

    testResource.reservationsRepositoryMock.createReservation.returns:
      case _ => IO.pure(CreateReservationResult.ReservationCreated)

    testResource.createReservationUseCase.createReservation(reservation).map {
      case Left(value) => failure(s"reservation should be valid, but got error $value")
      case Right(value) => expect.all(
        testResource.roomsRepositoryMock.findRoom.times == 1,
        testResource.reservationsRepositoryMock.findConflictingReservations.times == 1,
        testResource.reservationsRepositoryMock.createReservation.times == 1
      )
    }
  }
