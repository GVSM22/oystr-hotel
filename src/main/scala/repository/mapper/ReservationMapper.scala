package repository.mapper

import model.Reservation
import repository.entity.ReservationEntity

private [repository] object ReservationMapper:
  def toEntity(reservation: Reservation): ReservationEntity = 
    ReservationEntity(reservation.roomNumber, reservation.checkInDate, reservation.checkOutDate, reservation.guestName)
  def fromEntity(reservationEntity: ReservationEntity): Reservation =
    Reservation(reservationEntity.roomNumber, reservationEntity.checkInDate, reservationEntity.checkOutDate, reservationEntity.guestName)
