package com.illusion.ticketblitz.paymentservice.repository;

import com.illusion.ticketblitz.paymentservice.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByReservationId(String reservationId);

    Optional<PaymentRecord> findByExternalTransactionId(String externalTransactionId);
}