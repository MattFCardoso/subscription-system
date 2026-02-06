package com.globo.subscriptionapplication.domain.repository;

import com.globo.subscriptionapplication.domain.enums.PaymentStatusEnum;
import com.globo.subscriptionapplication.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findBySubscriptionSubscriptionId(UUID subscriptionId);

    List<Payment> findBySubscriptionSubscriptionIdAndStatus(UUID subscriptionId, PaymentStatusEnum status);

    @Query("SELECT p FROM Payment p WHERE p.subscription.subscriptionId = :subscriptionId ORDER BY p.paymentDate DESC")
    List<Payment> findBySubscriptionOrderByPaymentDateDesc(@Param("subscriptionId") UUID subscriptionId);

    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> findByStatus(PaymentStatusEnum status);
}
