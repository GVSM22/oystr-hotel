package repository

import cats.effect.IO
import model.{OperationConclusion, Room}
import repository.entity.RoomEntity
import repository.mapper.RoomMapper
import skunk.codec.all.int2
import skunk.data.Completion
import skunk.exception.PostgresErrorException
import skunk.{Command, Query, Session}
import skunk.implicits.sql

trait RoomsRepository:
  def findRoom(roomNumber: Short): IO[Option[Room]]
  def createRoom(roomNumber: Short): IO[OperationConclusion]
  def deleteRoom(roomNumber: Short): IO[Completion]

object RoomsRepository:
  
  private val `unique constraint violation` = "23505"
  
  def apply(session: Session[IO]): RoomsRepository = new RoomsRepository:
    override def findRoom(roomNumber: Short): IO[Option[Room]] =
      val q: Query[Short, RoomEntity] = sql"""SELECT room_number FROM rooms WHERE room_number = $int2""".query(RoomEntity.roomDecoder)
      session
        .prepare(q)
        .flatMap(_.option(roomNumber))
        .map(_.map(RoomMapper.fromEntity))

    override def createRoom(roomNumber: Short): IO[OperationConclusion] =
      val q: Command[Short] = sql"""INSERT INTO rooms VALUES ($int2)""".command
      session.execute(q)(roomNumber)
        .map(s => OperationConclusion.Created)
        .recover {
          case p: PostgresErrorException if p.code == `unique constraint violation` => OperationConclusion.AlreadyExist
        }

    override def deleteRoom(roomNumber: Short): IO[Completion] = ???