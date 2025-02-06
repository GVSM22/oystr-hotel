package model

import skunk.codec.all.int2
import skunk.{Decoder, Encoder}

case class Room(roomNumber: Short)
object Room:
  val roomDecoder: Decoder[Room] = int2.to[Room]
  val roomEncoder: Encoder[Room] = int2.values.to[Room]
