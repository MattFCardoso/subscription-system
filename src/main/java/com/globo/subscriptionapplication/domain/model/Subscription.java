package com.globo.subscriptionapplication.domain.model;

import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID subscriptionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PlanEnum plan;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SubscriptionStatusEnum status;

    @Builder.Default
    private int renewalAttempts = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void incrementRenewalAttempts() {
        this.status = SubscriptionStatusEnum.FALHA_PAGAMENTO;
        this.renewalAttempts++;
    }

    public Boolean attemptsExceeded() {
        return this.renewalAttempts >= 3;
    }

    public void suspendSubscription() {
        this.status = SubscriptionStatusEnum.SUSPENSA;
    }

    public void cancelSubscription() {
        this.status = SubscriptionStatusEnum.CANCELADA;
    }

    public void renewSubscription() {
        this.expirationDate = LocalDate.now().plusMonths(1);
        this.renewalAttempts = 0;
        this.status = SubscriptionStatusEnum.ATIVA;
    }

    public void startSubscription() {
        this.startDate = LocalDate.now();
        this.expirationDate = LocalDate.now().plusMonths(1);
        this.renewalAttempts = 0;
        this.status = SubscriptionStatusEnum.ATIVA;
    }

}
