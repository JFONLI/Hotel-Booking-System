package HotelBooking.api.repository;

import HotelBooking.api.repository.entity.Booking;
import HotelBooking.api.repository.entity.BookingStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface BookingsRepository
        extends JpaRepository<Booking, Long> {

    Optional<Booking> findBookingById(Long bookingId);

    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.paymentIntentId = :paymentIntentId")
    void updateStatusByPaymentIntentId(String paymentIntentId, String status);

    Booking getBookingByPaymentIntentId(String paymentIntentId);

    @Query(value = "SELECT b.status FROM bookings b WHERE b.payment_intent_id = :paymentIntentId", nativeQuery = true)
    BookingStatus getStatusByPaymentIntentId(String paymentIntentId);
}
