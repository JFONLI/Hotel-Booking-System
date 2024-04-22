package HotelBooking.api.service;

import HotelBooking.api.domain.HotelBookingSystemErrorCode;
import HotelBooking.api.domain.HotelBookingSystemException;
import HotelBooking.api.repository.BookingsRepository;
import HotelBooking.api.repository.entity.Booking;
import HotelBooking.api.repository.entity.BookingStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingsService {
    @Autowired
    private BookingsRepository bookingsRepository;
    @Autowired
    private RoomsService roomsService;
    @Autowired
    private StripeService stripeService;
    @Autowired
    private BookingCancellationService bookingCancellationService;

    public Booking getBookingDetails(String bookingId){
        return bookingsRepository.findBookingById(Long.valueOf(bookingId))
                .orElseThrow(() -> new HotelBookingSystemException(HttpStatus.NOT_FOUND, HotelBookingSystemErrorCode.BOOKING_NOT_FOUND));
    }

    public void updateBookingStatus(String paymentIntentId, String status){
        bookingsRepository.updateStatusByPaymentIntentId(paymentIntentId, status);
    }

    public ResponseEntity<String> makeABooking(HttpServletRequest request, LocalDate startDate, LocalDate endDate, int roomType, int noRooms) throws StripeException {
        // Check if the booking is available
        // If not
        // return response "No room available"
        // else
        // Insert the booking into bookingDB status = "BOOKED"
        // call the payment method
        // Modify the available room in the available_room table

        boolean isAvailable = roomsService.checkAvailability(startDate, endDate, roomType, noRooms);
        if(!isAvailable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No room available");
        }

        float price = calculatePrice(startDate, endDate, roomType, noRooms);
        Session session = stripeService.createSession(request, (long) (price*100), "usd", startDate, endDate, roomType, noRooms);
        String sessionId = session.getId();


        try {
            roomsService.updateAvailableRooms(startDate, endDate, roomType, noRooms);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }


        Booking booking = new Booking(sessionId, roomType, noRooms, BookingStatus.BOOKED, startDate, endDate, price, LocalDateTime.now());
        bookingsRepository.save(booking);

        bookingCancellationService.scheduleBookingCancellation(sessionId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", session.getUrl());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    public float calculatePrice(LocalDate startDate, LocalDate endDate, int roomType, int noRooms){
        float totalPrice = 0;
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            float dailyPrice = getRoomPrice(roomType, date);
            totalPrice += dailyPrice;
        }

        totalPrice *= noRooms;
        return totalPrice;
    }

    private float getRoomPrice(int roomType, LocalDate date) {
        return roomsService.findPriceByDateAndRoomType(date, roomType);
    }
}
