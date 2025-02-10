package usecase

import cats.effect.IO
import model.Reservation
import org.scalamock.stubs.{CatsEffectStubs, Stub}
import repository.{ReservationsRepository, RoomsRepository}
import usecase.occupancy.OccupancyUseCase
import weaver.SimpleIOSuite

import java.time.{LocalDate, LocalDateTime}
import scala.annotation.experimental

@experimental
object OccupancyTest extends SimpleIOSuite, CatsEffectStubs:
  class TestResource(
                      val occupancyUseCase: OccupancyUseCase,
                      val reservationsRepositoryMock: Stub[ReservationsRepository],
                      val roomsRepositoryMock: Stub[RoomsRepository]
                    )

  object TestResource:
    def apply(): TestResource =
      val reservationsMock = stub[ReservationsRepository]
      val roomsMock = stub[RoomsRepository]
      new TestResource(OccupancyUseCase(reservationsMock, roomsMock), reservationsMock, roomsMock)

  test("should return 0 when there's no room on database") {
    val testResource = TestResource()
    val date = LocalDate.now()

    testResource.roomsRepositoryMock.countRooms().returns(IO.pure(0L))
      
    testResource.reservationsRepositoryMock.findReservationsForDate.returns:
      case _ => IO.pure(List.empty)
      
    testResource.occupancyUseCase.getOccupancyForDay(date).map(_ =>
      expect.all(
        testResource.roomsRepositoryMock.countRooms().times == 1,
        testResource.reservationsRepositoryMock.findReservationsForDate.times == 1
      )
    )
  }
  
  test("should return occupancy for a given day") {
    val testResource = TestResource()
    val date = LocalDate.now()
    val reservation = Reservation(1, LocalDateTime.now(), LocalDateTime.now(), "name")

    testResource.roomsRepositoryMock.countRooms().returns(IO.pure(3L))

    testResource.reservationsRepositoryMock.findReservationsForDate.returns:
      case _ => IO.pure(List(reservation))

    testResource.occupancyUseCase.getOccupancyForDay(date).map(res =>
      expect.all(
        res == BigDecimal(33.3),
        testResource.roomsRepositoryMock.countRooms().times == 1,
        testResource.reservationsRepositoryMock.findReservationsForDate.times == 1
      )
    )
  }