package repository

import cats.effect.IO
import skunk.codec.all.int2
import skunk.{Query, Session}
import skunk.implicits.sql

trait RoomsRepository:
  def findRoom(roomNumber: Short): IO[Option[Short]]

object RoomsRepository:
  
  def apply(session: Session[IO]): RoomsRepository = new RoomsRepository:
    override def findRoom(roomNumber: Short): IO[Option[Short]] =
      val q: Query[Short, Short] = sql"""SELECT room_number FROM rooms WHERE room_number = $int2""".query(int2)
      session
        .prepare(q)
        .flatMap(_.option(roomNumber))
