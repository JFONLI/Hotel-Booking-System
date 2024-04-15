package HotelBooking.api.repository;

import HotelBooking.api.repository.entity.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface BookingsRepository
        extends JpaRepository<Booking, Long> {

    Booking findBookingById(Long bookingId);

    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.paymentIntentId = :paymentIntentId")
    void updateStatusByPaymentIntentId(String paymentIntentId, String status);

    Booking getBookingByPaymentIntentId(String paymentIntentId);

    @Query(value = "SELECT b.status FROM bookings b WHERE b.payment_intent_id = :paymentIntentId", nativeQuery = true)
    String getStatusByPaymentIntentId(String paymentIntentId);
}
