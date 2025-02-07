package repository.entity

import skunk.codec.all.{int2, timestamp, varchar}
import skunk.{Decoder, Encoder}

import java.time.LocalDateTime

private [repository] case class ReservationEntity(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime, guestName: String)
object ReservationEntity:
  val reservationDecoder: Decoder[ReservationEntity] = (int2 *: timestamp *: timestamp *: varchar).to[ReservationEntity]
  val reservationEncoder: Encoder[ReservationEntity] = (int2 *: timestamp *: timestamp *: varchar).values.to[ReservationEntity]