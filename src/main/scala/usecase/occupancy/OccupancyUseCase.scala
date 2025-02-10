package usecase.occupancy

import cats.effect.IO
import repository.{ReservationsRepository, RoomsRepository}

import java.time.LocalDate
import scala.math.BigDecimal.RoundingMode

case class OccupancyUseCase(private val reservationsRepository: ReservationsRepository, private val roomsRepository: RoomsRepository):

  def getOccupancyForDay(date: LocalDate): IO[BigDecimal] =
    val occupiedRooms = reservationsRepository.findReservationsForDate(date).map(
      _.groupBy(_.roomNumber).size
    )

    for {
      rooms <- roomsRepository.countRooms()
      occupied <- occupiedRooms
      percentage = if (rooms == 0) BigDecimal(0) else percentageOfOccupiedRooms(rooms, occupied)
    } yield percentage

  private def percentageOfOccupiedRooms(roomsQuantity: Long, occupiedQuantity: Int): BigDecimal =
    (BigDecimal(occupiedQuantity) / BigDecimal(roomsQuantity) * 100).setScale(1, RoundingMode.HALF_UP)