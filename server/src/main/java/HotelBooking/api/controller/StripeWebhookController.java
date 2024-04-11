package HotelBooking.api.controller;

import HotelBooking.api.service.BookingsService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {
    @Value("${stripe.private.key}")
    private String apiKey;
    private static final String STRIPE_WEBHOOK_SECRET = "whsec_5937aabedb5198da0b52f699c893d05bddb294bdf164c50aaf8e06324b2b20a5";

    @Autowired
    private BookingsService bookingsService;


    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader){
        try{
            Event event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);

            switch(event.getType()){
                case "payment_intent.succeeded" : {
                    return handlePaymentSucceededEvent(event);
                }
                case "payment_intent.payment_failed" : {
                    handlePaymentFailedEvent(event);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed to be completed");
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body("Received and processed Stripe webhook successfully.");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Stripe Signature");
        }
    }

    private ResponseEntity<String> handlePaymentSucceededEvent(Event event){
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if(dataObjectDeserializer.getObject().isPresent()){
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        assert paymentIntent != null;
        String paymentIntentId = paymentIntent.getId();

        bookingsService.updateBookingStatus(paymentIntentId, "PAID");

        return ResponseEntity.status(HttpStatus.OK).body("Received and processed payment successfully");
    }

    private void handlePaymentFailedEvent(Event event){

    }
}
