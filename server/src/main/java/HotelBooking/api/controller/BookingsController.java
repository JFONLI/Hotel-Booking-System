package HotelBooking.api.controller;

import HotelBooking.api.repository.RoomsRepository;
import HotelBooking.api.repository.entity.Booking;
import HotelBooking.api.service.BookingsService;
import HotelBooking.api.service.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(path = "/v1/bookings")
public class BookingsController {

    private final BookingsService bookingsService;

    @Autowired
    public BookingsController(BookingsService bookingsService){
        this.bookingsService = bookingsService;
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Booking> getBookingDetails(@PathVariable("bookingId") String bookingId){
        Booking booking = bookingsService.getBookingDetails(bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping
    public ResponseEntity<?> makeABooking(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam int roomType, @RequestParam int noRooms) throws StripeException {
        return bookingsService.makeABooking(startDate, endDate, roomType, noRooms);
    }

    @GetMapping(path = "/stripe_payment")
    public ResponseEntity<?> stripe_payment(@RequestParam Long amount, @RequestParam String currency){
        try {
            String clientSecret = bookingsService.initiatePayment(amount, currency);
            return ResponseEntity.ok(clientSecret);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment.");
        }
    }

}
