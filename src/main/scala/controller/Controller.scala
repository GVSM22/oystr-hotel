package controller

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.circe.{CirceEntityDecoder, CirceEntityEncoder}

trait Controller extends CirceEntityDecoder, CirceEntityEncoder:
  val routes: HttpRoutes[IO]
