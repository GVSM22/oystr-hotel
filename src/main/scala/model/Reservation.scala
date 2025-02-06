package model

import skunk.codec.all.{int2, timestamp, varchar}
import skunk.{Decoder, Encoder}

import java.time.LocalDateTime

case class Reservation(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime, guestName: String)
object Reservation:
  val reservationDecoder: Decoder[Reservation] = (int2 *: timestamp *: timestamp *: varchar).to[Reservation]
  val reservationEncoder: Encoder[Reservation] = (int2 *: timestamp *: timestamp *: varchar).values.to[Reservation]