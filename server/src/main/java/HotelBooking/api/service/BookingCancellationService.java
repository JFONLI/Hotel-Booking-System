package HotelBooking.api.service;

import HotelBooking.api.repository.BookingsRepository;
import HotelBooking.api.repository.RoomsRepository;
import HotelBooking.api.repository.entity.Booking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BookingCancellationService {

    @Autowired
    private BookingsRepository bookingsRepository;
    @Autowired
    private RoomsRepository roomsRepository;
    @Autowired
    private StripeService stripeService;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);

    @PersistenceContext
    private EntityManager entityManager;

    public void scheduleBookingCancellation(String bookingId){
        // scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            String status = bookingsRepository.getStatusByPaymentIntentId(bookingId);
            if(status.equals("BOOKED")){
                stripeService.cancelPaymentIntent(bookingId);
                bookingsRepository.updateStatusByPaymentIntentId(bookingId, "CANCELED");
                Booking booking = bookingsRepository.getBookingByPaymentIntentId(bookingId);
                roomsRepository.releaseAvailableRooms(booking.getStart_date(), booking.getEnd_date(), booking.getRoom_type(), booking.getNo_rooms());
            }
        }, 60, TimeUnit.MINUTES);
    }
}
