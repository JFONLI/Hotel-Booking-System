package HotelBooking.api.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionExpireParams;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StripeService {
    @Value("${stripe.private.key}")
    private String apiKey;


    public Session createSession(HttpServletRequest request, Long amount, String currency, LocalDate startDate, LocalDate endDate, int roomType, int noRooms){
        Stripe.apiKey = apiKey;
        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                            .setSuccessUrl("http://" + request.getServerName() + ":8080/v1/bookings/success")
                            .setCancelUrl("http://" + request.getServerName() + ":8080/v1/bookings/fail")
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency(currency)
                                                            .setUnitAmount(amount)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName(generateProductName(startDate, endDate, roomType, noRooms))
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .setQuantity(1L)
                                            .build()
                            )
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .build();
            Session session = Session.create(params);
            System.out.println(session.getUrl());
            return session;
        } catch (StripeException e) {
            System.out.println("Failed to create Session : " + e.getMessage());
            return null;
        }
    }

    public void expireSession(String sessionId){
        Stripe.apiKey = apiKey;
        try {
            Session resource = Session.retrieve(sessionId);
            SessionExpireParams params = SessionExpireParams.builder().build();
            Session session = resource.expire(params);
        } catch (StripeException e) {
            System.out.println("Failed to expire Session : " + e.getMessage());
        }
    }

    public String createPaymentIntent(Long amount, String currency) {
        Stripe.apiKey = apiKey;
        try {
            PaymentIntent intent = PaymentIntent.create(new PaymentIntentCreateParams.Builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setPaymentMethod("pm_card_visa")
                    .build());
            System.out.println(intent.getClientSecret());
            return intent.getClientSecret();
        } catch (StripeException e){
            e.printStackTrace();
            return "error";
        }
    }

    public void confirmPaymentIntent(String paymentIntentId) throws StripeException {
        Stripe.apiKey = apiKey;
        try {
            PaymentIntent resource = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntentConfirmParams params =
                    PaymentIntentConfirmParams.builder()
                            .setPaymentMethod("pm_card_visa")
                            .setReturnUrl("http://localhost:8080/bookings/success")
                            .build();

            PaymentIntent paymentIntent = resource.confirm(params);
            // System.out.println(paymentIntent.toJson());
        } catch (StripeException e) {
            System.out.println("Failed to confirm Payment Intent: " + e.getMessage());
        }
    }

    public void cancelPaymentIntent(String paymentIntentId) {
        Stripe.apiKey = apiKey;
        try {
            PaymentIntent resource = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntentCancelParams params = PaymentIntentCancelParams.builder().build();
            PaymentIntent paymentIntent = resource.cancel(params);

        } catch (StripeException e) {
            System.out.println("Faild to cancel Payment Intent : " + e.getMessage());
        }
    }

    public String extractPaymentIntentId(String clientSecret) {
        if (clientSecret != null && clientSecret.contains("_secret_")) {
            return clientSecret.split("_secret_")[0];
        } else {
            return null;
        }
    }

    public static String extractClientSecretKey(String clientSecret) {
        if (clientSecret != null && clientSecret.contains("_secret_")) {
            return clientSecret.split("_secret_")[1];
        } else {
            return null;
        }
    }

    public static String generateProductName(LocalDate startDate, LocalDate endDate, int roomType, int noRooms) {
        String roomTypeStr = "";
        if (roomType == 1) {
            roomTypeStr = "Standard Room";
        } else if (roomType == 2) {
            roomTypeStr = "Deluxe Room";
        } else if (roomType == 3) {
            roomTypeStr = "Suite";
        }
        return String.format("%s - %s to %s, %d rooms", roomTypeStr, startDate.toString(), endDate.toString(), noRooms);
    }

}
