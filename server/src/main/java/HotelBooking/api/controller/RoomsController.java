package HotelBooking.api.controller;

import HotelBooking.api.repository.entity.Room;
import HotelBooking.api.service.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping(path = "/v1/rooms")
public class RoomsController {
    private final RoomsService roomsService;
    @Autowired
    public RoomsController(RoomsService roomsService){
        this.roomsService = roomsService;
    }

    @GetMapping
    public List<Room> getAllData(@RequestParam LocalDate start_date, @RequestParam LocalDate end_date){
        return roomsService.getAllData(start_date, end_date);
    }
}
