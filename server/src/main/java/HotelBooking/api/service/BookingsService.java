package HotelBooking.api.service;

import HotelBooking.api.repository.BookingsRepository;
import HotelBooking.api.repository.RoomsRepository;
import HotelBooking.api.repository.entity.Booking;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BookingsService {
    private final BookingsRepository bookingsRepository;
    private final RoomsRepository roomsRepository;
    private final StripeService stripeService;
    private final BookingCancellationService bookingCancellationService;

    @Autowired
    public BookingsService(StripeService stripeService, RoomsRepository roomsRepository, BookingsRepository bookingsRepository, BookingCancellationService bookingCancellationService){
        this.bookingsRepository = bookingsRepository;
        this.roomsRepository = roomsRepository;
        this.stripeService = stripeService;
        this.bookingCancellationService = bookingCancellationService;
    }

    public Booking getBookingDetails(String bookingId){
        return bookingsRepository.findBookingById(Long.valueOf(bookingId));
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

        boolean isAvailable = roomsRepository.checkAvailability(startDate, endDate, roomType, noRooms);
        if(!isAvailable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No room available");
        }

        float price = calculatePrice(startDate, endDate, roomType, noRooms);
        Session session = stripeService.createSession(request, (long) (price*100), "usd", startDate, endDate, roomType, noRooms);
        String sessionId = session.getId();


        try {
            roomsRepository.updateAvailableRooms(startDate, endDate, roomType, noRooms);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }


        Booking booking = new Booking(sessionId, roomType, noRooms, "BOOKED", startDate, endDate, price, LocalDateTime.now());
        bookingsRepository.save(booking);

        bookingCancellationService.scheduleBookingCancellation(sessionId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", session.getUrl());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
        //return ResponseEntity.status(HttpStatus.CREATED).body("Successfully make a Booking");
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
        return roomsRepository.findPriceByDateAndRoomType(date, roomType);
    }
}
