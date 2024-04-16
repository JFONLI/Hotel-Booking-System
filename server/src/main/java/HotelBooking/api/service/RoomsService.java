package HotelBooking.api.service;

import HotelBooking.api.repository.entity.Room;
import HotelBooking.api.repository.RoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

@Service
public class RoomsService {
    private final RoomsRepository roomsRepository;

    @Autowired
    public RoomsService(RoomsRepository roomsRepository){
        this.roomsRepository = roomsRepository;
    }
    public List<Room> getAvailableRooms(LocalDate start_date, LocalDate end_date) {
        return roomsRepository.findByDateRange(start_date, end_date);
    }
}
