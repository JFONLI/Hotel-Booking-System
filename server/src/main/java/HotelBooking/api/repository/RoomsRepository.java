package HotelBooking.api.repository;

import HotelBooking.api.repository.entity.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomsRepository
    extends JpaRepository<Room, Long> {

    @Query(value = "SELECT * FROM available_rooms r WHERE r.date >= :startDate AND r.date <= :endDate", nativeQuery = true)
    List<Room> findByDateRange(LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT no_rooms FROM available_rooms r WHERE r.date >= :startDate AND r.date <= :endDate AND r.room_type = :room_type", nativeQuery = true)
    List<Integer> findAvailability(int room_type, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT COUNT(*) FROM available_rooms r WHERE r.date BETWEEN :startDate AND :endDate AND r.room_type = :roomType AND r.no_rooms < :noRooms", nativeQuery = true)
    int countUnavailableRooms(LocalDate startDate, LocalDate endDate, int roomType, int noRooms);

    default boolean checkAvailability(LocalDate startDate, LocalDate endDate, int roomType, int noRooms) {
        int unavailableRooms = countUnavailableRooms(startDate, endDate, roomType, noRooms);
        return unavailableRooms == 0;
    }

    @Query(value = "SELECT rates FROM available_rooms r WHERE r.date = :date AND r.room_type = :roomType", nativeQuery = true)
    Float findPriceByDateAndRoomType(LocalDate date, int roomType);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE available_rooms SET no_rooms = no_rooms + :noRooms " +
//            "WHERE room_type = :roomType AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
//    void releaseAvailableRooms(LocalDate startDate, LocalDate endDate, int roomType, int noRooms);

    @Transactional
    default void updateAvailableRooms(LocalDate startDate, LocalDate endDate, int roomType, int noRooms) {
        List<Room> rooms = findByDateRange(startDate, endDate);
        rooms.forEach(room -> {
            if (room.getRoom_type() == roomType) {
                int updatedNoRooms = room.getNo_rooms() - noRooms;
                if (updatedNoRooms >= 0) {
                    room.setNo_rooms(updatedNoRooms);
                    save(room);
                } else {
                    throw new RuntimeException("No rooms available, total will comes to 0");
                }
            }
        });
    }

    @Transactional
    default void releaseAvailableRooms(LocalDate startDate, LocalDate endDate, int roomType, int noRooms) {
        List<Room> rooms = findByDateRange(startDate, endDate);
        rooms.forEach(room -> {
            if (room.getRoom_type() == roomType) {
                int updatedNoRooms = room.getNo_rooms() + noRooms;
                room.setNo_rooms(updatedNoRooms);
                save(room);
            }
        });
    }

}
