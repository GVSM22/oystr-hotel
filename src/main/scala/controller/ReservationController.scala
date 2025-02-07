package controller

import cats.effect.IO
import org.http4s.HttpRoutes

case class ReservationController() extends Controller:
  override val routes: HttpRoutes[IO] = HttpRoutes.empty