package com.illusion.ticketblitz.paymentservice.gateway;

public record PaymentResult(
        boolean success,
        String transactionId, // External payment provider ID (Stripe/Razorpay)
        String errorMessage   // Error code when success=false
) {}