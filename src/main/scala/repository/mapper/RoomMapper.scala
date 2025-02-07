package repository.mapper

import model.Room
import repository.entity.RoomEntity

private [repository] object RoomMapper:
  def fromEntity(roomEntity: RoomEntity): Room = Room(roomEntity.roomNumber)