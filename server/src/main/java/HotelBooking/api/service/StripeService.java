package HotelBooking.api.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Value("${stripe.private.key}")
    private String apiKey;

    public String createPaymentIntent(Long amount, String currency) {
        Stripe.apiKey = apiKey;
        try {
            PaymentIntent intent = PaymentIntent.create(new PaymentIntentCreateParams.Builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setPaymentMethod("pm_card_visa")
                    .build());
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
        } catch (StripeException e){
            System.out.println("Error: " + e.getMessage());
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

}
