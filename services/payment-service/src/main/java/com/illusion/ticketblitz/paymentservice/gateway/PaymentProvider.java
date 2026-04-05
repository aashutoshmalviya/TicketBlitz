package com.illusion.ticketblitz.paymentservice.gateway;

import java.math.BigDecimal;

public interface PaymentProvider {
    PaymentResult processPayment(String userId, BigDecimal amount, String currency);
}