package controller.mapper

import controller.request.ReservationRequest
import model.Reservation

private [controller] object ReservationMapper:
  def fromRequest(reservationRequest: ReservationRequest): Reservation =
    Reservation(reservationRequest.roomNumber, reservationRequest.checkInDate, reservationRequest.checkOutDate, reservationRequest.guestName)
