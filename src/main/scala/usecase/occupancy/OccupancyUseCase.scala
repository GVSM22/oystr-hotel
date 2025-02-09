package usecase.occupancy

import cats.effect.IO
import model.Reservation
import repository.{ReservationsRepository, RoomsRepository}

import java.time.LocalDate
import scala.math.BigDecimal.RoundingMode

case class OccupancyUseCase(reservationsRepository: ReservationsRepository, roomsRepository: RoomsRepository):

  def getOccupancyForDay(date: LocalDate): IO[BigDecimal] =
    val occupiedRooms = reservationsRepository.findReservationsForDate(date)
    val roomTotal = roomsRepository.getAllRooms

    val totalOfOccupiedRooms = occupiedRooms.map(
      _.groupBy(_.roomNumber)
        .collect {
          case (k, head :: tail) => k -> head
        }
        .size
    )

    for {
      rooms <- roomTotal
      occupied <- totalOfOccupiedRooms
    } yield (BigDecimal(occupied) / BigDecimal(rooms) * 100).setScale(1, RoundingMode.HALF_UP)