package controller

import cats.effect.IO
import org.http4s.HttpRoutes

trait Controller:
  val routes: HttpRoutes[IO]
