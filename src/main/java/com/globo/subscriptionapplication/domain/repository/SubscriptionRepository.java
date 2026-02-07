package com.globo.subscriptionapplication.domain.repository;

import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.domain.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT s FROM Subscription s WHERE s.user.userId = :userId AND s.status = :status")    Optional<Subscription> findByUserIdAndStatus(
            @Param("userId") UUID userId,
            @Param("status") SubscriptionStatusEnum status);

    boolean existsByUserAndStatus(User user, SubscriptionStatusEnum status);

    @Query("SELECT s FROM Subscription s WHERE s.expirationDate <= :date AND s.status = :status")
    List<Subscription> findExpiredSubscriptions(
            @Param("date") LocalDate date,
            @Param("status") SubscriptionStatusEnum status);

    // Lock pessimista para evitar condições de corrida na renovação
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Subscription s WHERE s.subscriptionId = :subscriptionId")
    Optional<Subscription> findByIdWithLock(@Param("subscriptionId") UUID subscriptionId);

    @Query("SELECT s FROM Subscription s WHERE s.expirationDate <= CURRENT_DATE AND s.status = 'ATIVA' AND s.renewalAttempts < 3")
    List<Subscription> findAllExpired();
}
