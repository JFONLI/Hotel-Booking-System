package HotelBooking.api.service;

import HotelBooking.api.repository.BookingsRepository;
import HotelBooking.api.repository.RoomsRepository;
import HotelBooking.api.repository.entity.Booking;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookingsService {
    private final BookingsRepository bookingsRepository;
    private final RoomsRepository roomsRepository;
    private final StripeService stripeService;


    @Autowired
    public BookingsService(StripeService stripeService, RoomsRepository roomsRepository, BookingsRepository bookingsRepository){
        this.bookingsRepository = bookingsRepository;
        this.roomsRepository = roomsRepository;
        this.stripeService = stripeService;
    }

    public Booking getBookingDetails(String bookingId){
        return bookingsRepository.findBookingById(Long.valueOf(bookingId));
    }

    public String initiatePayment(Long amount, String currency) throws StripeException {
        return stripeService.createPaymentIntent(amount, currency);
    }

    public String getPaymentStatus(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.getStatus();
    }

    public void updateBookingStatus(String paymentIntentId, String status){
        bookingsRepository.updateStatusByPaymentIntentId(paymentIntentId, status);
    }

    public ResponseEntity<String> makeABooking(LocalDate startDate, LocalDate endDate, int roomType, int noRooms) throws StripeException {
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
        String paymentIntentId = initiatePayment((long) (price*100), "usd");
        Booking booking = new Booking(paymentIntentId, roomType, noRooms, "BOOKED", startDate, endDate, price);
        bookingsRepository.save(booking);
        roomsRepository.updateAvailableRooms(startDate, endDate, roomType, noRooms);

        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully make a Booking");
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
