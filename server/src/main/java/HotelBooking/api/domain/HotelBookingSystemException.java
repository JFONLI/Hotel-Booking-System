package HotelBooking.api.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HotelBookingSystemException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final String errorCode;

    public HotelBookingSystemException(HttpStatus httpStatus, HotelBookingSystemErrorCode code){
        super(code.getMessage());
        this.httpStatus = httpStatus;
        this.errorCode = code.getCode();
    }

}
