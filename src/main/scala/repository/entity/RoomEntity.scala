package repository.entity

import skunk.codec.all.int2
import skunk.{Decoder, Encoder}

case class RoomEntity(roomNumber: Short)

object RoomEntity:
  val roomDecoder: Decoder[RoomEntity] = int2.to[RoomEntity]
  val roomEncoder: Encoder[RoomEntity] = int2.values.to[RoomEntity]
