package HotelBooking.api.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "payment_intent_id")
    private String paymentIntentId;
    @Column(name = "room_type")
    private int room_type;
    @Column(name = "no_rooms")
    private int no_rooms;
    @Column(name = "status")
    private String status;
    @Column(name = "start_date")
    private LocalDate start_date;
    @Column(name = "end_date")
    private LocalDate end_date;
    @Column(name = "price")
    private float price;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    public Booking(String paymentIntentId, int roomType, int noRooms, String status, LocalDate startDate, LocalDate endDate, float price, LocalDateTime createDate) {
        this.paymentIntentId = paymentIntentId;
        this.room_type = roomType;
        this.no_rooms = noRooms;
        this.status = status;
        this.start_date = startDate;
        this.end_date = endDate;
        this.price = price;
        this.createDate = createDate;
    }
}
