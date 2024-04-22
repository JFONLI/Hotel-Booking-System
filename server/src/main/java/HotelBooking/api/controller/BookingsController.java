package HotelBooking.api.controller;

import HotelBooking.api.domain.HotelBookingSystemException;
import HotelBooking.api.repository.entity.Booking;
import HotelBooking.api.service.BookingsService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(path = "/v1/bookings")
public class BookingsController {

    @Autowired
    private BookingsService bookingsService;

    @GetMapping(path="/success")
    public String success(){
        return "success";
    }

    @GetMapping(path="/fail")
    public String fail(){
        return "fail";
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<?> getBookingDetails(@PathVariable("bookingId") String bookingId){
        try{
            return ResponseEntity.ok(bookingsService.getBookingDetails(bookingId));
        } catch (HotelBookingSystemException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> makeABooking(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam int roomType, @RequestParam int noRooms, HttpServletRequest request) throws StripeException {
        return bookingsService.makeABooking(request, startDate, endDate, roomType, noRooms);
    }
}
