package HotelBooking.api.controller;

import HotelBooking.api.repository.entity.Room;
import HotelBooking.api.service.RoomsService;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping(path = "/v1/rooms")
public class RoomsController {

    @Autowired
    private RoomsService roomsService;

    @GetMapping
    public List<Room> getAvailableRooms(@RequestParam LocalDate start_date, @RequestParam LocalDate end_date){
        return roomsService.getAvailableRooms(start_date, end_date);
    }
}
