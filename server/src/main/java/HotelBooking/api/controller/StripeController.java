package HotelBooking.api.controller;

import HotelBooking.api.repository.entity.Room;
import HotelBooking.api.service.RoomsService;
import HotelBooking.api.service.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/stripe")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @GetMapping(path = "/confirm")
    public void confirmPaymentIntent(@RequestParam String paymentIntentId) throws StripeException {
        stripeService.confirmPaymentIntent(paymentIntentId);
    }
}