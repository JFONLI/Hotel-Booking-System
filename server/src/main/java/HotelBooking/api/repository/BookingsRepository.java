package HotelBooking.api.repository;

import HotelBooking.api.repository.entity.Booking;
import HotelBooking.api.repository.entity.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface BookingsRepository
        extends JpaRepository<Booking, Long> {

    Booking findBookingById(Long bookingId);

    @Query("UPDATE Booking b SET b.status = :status WHERE b.paymentIntentId = :paymentIntentId")
    Optional<Integer> updateStatusByPaymentIntentId(String paymentIntentId, String status);
}
