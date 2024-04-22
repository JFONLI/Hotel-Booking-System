package HotelBooking.api.domain;

import lombok.Getter;

@Getter
public enum HotelBookingSystemErrorCode {

    BOOKING_NOT_FOUND("10001", "Booking not found");

    private final String code;
    private final String message;

    HotelBookingSystemErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }
}
