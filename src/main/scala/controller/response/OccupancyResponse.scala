package controller.response

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class OccupancyResponse(percentage: BigDecimal)

object OccupancyResponse:
  given Decoder[OccupancyResponse] = deriveDecoder
  given Encoder[OccupancyResponse] = deriveEncoder
