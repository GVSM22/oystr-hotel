package controller.request

import io.circe.derivation.{Configuration, ConfiguredDecoder, ConfiguredEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class ReservationRequest(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime, guestName: String)

object ReservationRequest:
  given Configuration = Configuration.default.withSnakeCaseMemberNames
  given Decoder[ReservationRequest] = ConfiguredDecoder.derived
  given Encoder[ReservationRequest] = ConfiguredEncoder.derived
