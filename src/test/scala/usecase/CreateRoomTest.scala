package usecase

import cats.effect.IO
import org.scalamock.stubs.{CatsEffectStubs, Stub}
import repository.RoomsRepository
import usecase.createRoom.{CreateRoomUseCase, model}
import usecase.createRoom.model.CreateRoomResult
import weaver.SimpleIOSuite

import scala.annotation.experimental

@experimental
object CreateRoomTest extends SimpleIOSuite, CatsEffectStubs:

  class TestResource(
                      val roomsRepositoryMock: Stub[RoomsRepository],
                      val useCase: CreateRoomUseCase
                    )
  object TestResource:
    def apply(): TestResource =
      val repository = stub[RoomsRepository]
      new TestResource(repository, CreateRoomUseCase(repository))

  test("should create a room from a room number") {
    val testResource = TestResource()
    val roomNumber: Short = 1

    testResource.roomsRepositoryMock.createRoom.returns:
      case _ => IO.pure(CreateRoomResult.Created)

    testResource.useCase.createRoom(roomNumber).map {
      case model.CreateRoomResult.Created =>
        expect(testResource.roomsRepositoryMock.createRoom.times == 1)
      case model.CreateRoomResult.AlreadyExist =>
        failure("roomsRepositoryMock.createRoom wasn't mocked correctly")
    }
  }

  test("should return a different response when trying to create a duplicate room") {
    val testResource = TestResource()
    val roomNumber: Short = 1

    testResource.roomsRepositoryMock.createRoom.returns:
      case _ => IO.pure(CreateRoomResult.AlreadyExist)

    testResource.useCase.createRoom(roomNumber).map {
      case model.CreateRoomResult.Created =>
        failure("roomsRepositoryMock.createRoom wasn't mocked correctly")
      case model.CreateRoomResult.AlreadyExist =>
        expect(testResource.roomsRepositoryMock.createRoom.times == 1)
    }
  }