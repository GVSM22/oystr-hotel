package controller.request

import io.circe.derivation.{Configuration, ConfiguredCodec}
import io.circe.Codec
import org.http4s.circe.{CirceEntityDecoder, CirceEntityEncoder}

import java.time.LocalDateTime

case class ReservationRequest(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime, guestName: String)

object ReservationRequest extends CirceEntityDecoder with CirceEntityEncoder:
  given Configuration = Configuration.default.withSnakeCaseMemberNames
  val decoder: Codec[ReservationRequest] = ConfiguredCodec.derived
