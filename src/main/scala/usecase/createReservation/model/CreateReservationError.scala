package usecase.createReservation.model

enum CreateReservationError:
  case InvalidRoom, ConflictingReservation
