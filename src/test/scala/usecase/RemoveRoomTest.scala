package usecase

import cats.effect.IO
import org.scalamock.stubs.{CatsEffectStubs, Stub}
import repository.RoomsRepository
import repository.exception.PostgresException
import usecase.removeRoom.RemoveRoomUseCase
import usecase.removeRoom.model.RemoveRoomResult
import weaver.SimpleIOSuite

import scala.annotation.experimental

@experimental
object RemoveRoomTest extends SimpleIOSuite, CatsEffectStubs:

  class TestResource(
                      val roomsRepositoryMock: Stub[RoomsRepository],
                      val useCase: RemoveRoomUseCase
                    )

  object TestResource:
    def apply(): TestResource =
      val repository = stub[RoomsRepository]
      new TestResource(repository, RemoveRoomUseCase(repository))

  test("should not be possible to remove a room that have a reservation") {
    val testResource = TestResource()
    val roomNumber: Short = 1

    testResource.roomsRepositoryMock.deleteRoom.returns:
      case _ => IO.raiseError(PostgresException.ForeignKeyConstraintViolation)

    testResource.useCase.removeRoom(roomNumber).map {
      case Left(value) =>
        expect(testResource.roomsRepositoryMock.deleteRoom.times == 1)
      case Right(value) =>
        failure("maybe testResource.roomsRepositoryMock wasn't correctly mocked")
    }
  }

  test("should remove a valid room") {
    val testResource = TestResource()
    val roomNumber: Short = 1

    testResource.roomsRepositoryMock.deleteRoom.returns:
      case _ => IO.pure(RemoveRoomResult.Success)

    testResource.useCase.removeRoom(roomNumber).map {
      case Left(value) =>
        failure(s"unexpected error on a 'happy path' test = $value")
      case Right(value) =>
        expect(testResource.roomsRepositoryMock.deleteRoom.times == 1)
    }
  }