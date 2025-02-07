package exception

trait CustomError
object CustomError:
  object InvalidRoom extends CustomError
  object ConflictingReservation extends CustomError
