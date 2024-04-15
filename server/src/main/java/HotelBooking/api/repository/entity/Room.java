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
@Table(name = "available_rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Version
    private int version;
    @Column(name = "room_type")
    private int room_type;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "no_rooms")
    private int no_rooms;
    @Column(name = "rates")
    private float rates;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
