package com.illusion.ticketblitz.paymentservice.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MockStripeProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(MockStripeProvider.class);

    @Override
    public PaymentResult processPayment(String userId, BigDecimal amount, String currency) {
        log.info("Processing payment for user {}: {} {}", userId, amount, currency);

        try {
            // Simulate network latency - real Stripe calls take 500ms-1.5s
            Thread.sleep(ThreadLocalRandom.current().nextLong(500, 1500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mock 80% success rate for testing
        boolean isSuccess = ThreadLocalRandom.current().nextInt(100) < 80;

        if (isSuccess) {
            String transactionId = "ch_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
            log.info("Payment successful, transaction: {}", transactionId);
            return new PaymentResult(true, transactionId, null);
        } else {
            log.warn("Payment failed: card declined");
            return new PaymentResult(false, null, "card_declined");
        }
    }
}