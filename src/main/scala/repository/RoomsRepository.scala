package repository

import cats.effect.IO
import cats.syntax.all.catsSyntaxMonadError
import model.Room
import repository.entity.RoomEntity
import repository.exception.PostgresException
import repository.mapper.RoomMapper
import skunk.Void
import skunk.codec.all.{int2, int8}
import skunk.exception.PostgresErrorException
import skunk.implicits.sql
import skunk.{Command, Query, Session}
import usecase.createRoom.model.CreateRoomResult
import usecase.removeRoom.model.RemoveRoomResult

trait RoomsRepository:
  def findRoom(roomNumber: Short): IO[Option[Room]]
  def createRoom(roomNumber: Short): IO[CreateRoomResult]
  def deleteRoom(roomNumber: Short): IO[RemoveRoomResult]
  def getAllRooms: IO[Long]

object RoomsRepository:
  
  private val `unique constraint violation` = "23505"
  private val `foreign key constraint violation` = "23503"
  
  def apply(session: Session[IO]): RoomsRepository = new RoomsRepository:
    override def findRoom(roomNumber: Short): IO[Option[Room]] =
      val q: Query[Short, RoomEntity] = sql"""SELECT room_number FROM rooms WHERE room_number = $int2""".query(RoomEntity.roomDecoder)
      session
        .prepare(q)
        .flatMap(_.option(roomNumber))
        .map(_.map(RoomMapper.fromEntity))

    override def createRoom(roomNumber: Short): IO[CreateRoomResult] =
      val q: Command[Short] = sql"""INSERT INTO rooms VALUES ($int2)""".command
      session.execute(q)(roomNumber)
        .map(s => CreateRoomResult.Created)
        .adaptError {
          case p: PostgresErrorException => PostgresException(p.code)
        }

    override def deleteRoom(roomNumber: Short): IO[RemoveRoomResult] =
      val q: Command[Short] = sql"""DELETE from rooms WHERE room_number = $int2""".command
      session.execute(q)(roomNumber)
        .map(s => RemoveRoomResult.Deleted)
        .adaptError {
          case p: PostgresErrorException => PostgresException(p.code)
        }

    override def getAllRooms: IO[Long] =
      val q: Query[Void, Long] = sql"""SELECT COUNT(*) FROM rooms""".query(int8)
      session.unique(q)
