package model

import java.time.LocalDateTime

case class Reservation(roomNumber: Short, checkInDate: LocalDateTime, checkOutDate: LocalDateTime, guestName: String)
