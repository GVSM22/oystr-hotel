package middleware

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import org.http4s.{HttpRoutes, Request}

object LogErrorsMiddleware:
  def apply(app: HttpRoutes[IO]): HttpRoutes[IO] =
    Kleisli { (req: Request[IO]) =>
      OptionT.apply(app.run(req).value.onError(IO.println))
    }
