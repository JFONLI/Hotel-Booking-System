package HotelBooking.api.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Value("${stripe.private.key}")
    private String apiKey;

    public String createPaymentIntent(Long amount, String currency) throws StripeException {
        Stripe.apiKey = "sk_test_51P4BMvClFjyETeae6pCUENVwCDgDQ4jHnOxFWuBQJNja0kLVBqktq9FMXZNWQVWifUKXzTwdmjFfjbBm8oiebVsF006dsZCpFQ";
        PaymentIntent intent = PaymentIntent.create(new PaymentIntentCreateParams.Builder()
                .setAmount(amount)
                .setCurrency(currency)
                .build());

        return intent.getClientSecret();
    }

}
