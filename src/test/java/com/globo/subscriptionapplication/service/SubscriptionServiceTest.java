//package com.globo.subscriptionapplication.service;
//
//import com.globo.subscriptionapplication.domain.enums.PlanEnum;
//import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;
//import com.globo.subscriptionapplication.domain.model.Subscription;
//import com.globo.subscriptionapplication.domain.model.User;
//import com.globo.subscriptionapplication.domain.repository.SubscriptionRepository;
//import com.globo.subscriptionapplication.domain.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SubscriptionServiceTest {
//
//    @Mock
//    private SubscriptionRepository subscriptionRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private SubscriptionServiceImpl subscriptionServiceImpl;
//
//    private User testUser;
//    private Subscription testSubscription;
//    private UUID testUserId;
//    private UUID testSubscriptionId;
//
//    @BeforeEach
//    void setUp() {
//        testUserId = UUID.randomUUID();
//        testSubscriptionId = UUID.randomUUID();
//
//        testUser = User.builder()
//                .userId(testUserId)
//                .name("Test User")
//                .email("test@example.com")
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        testSubscription = Subscription.builder()
//                .subscriptionId(testSubscriptionId)
//                .userId(testUser)
//                .plan(PlanEnum.BASICO)
//                .startDate(LocalDate.now())
//                .expirationDate(LocalDate.now().plusMonths(1))
//                .status(SubscriptionStatusEnum.ATIVA)
//                .renewalAttempts(0)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    void createSubscription_Success() {
//        // Arrange
//        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
//        when(subscriptionRepository.existsByUserIdAndStatus(testUserId, SubscriptionStatusEnum.ATIVA)).thenReturn(false);
//        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);
//
//        // Act
//        Subscription result = subscriptionServiceImpl.createSubscription(testUserId, PlanEnum.BASICO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(PlanEnum.BASICO, result.getPlan());
//        assertEquals(SubscriptionStatusEnum.ATIVA, result.getStatus());
//        verify(subscriptionRepository).save(any(Subscription.class));
//    }
//
//    @Test
//    void createSubscription_UserNotFound() {
//        // Arrange
//        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(RuntimeException.class,
//                () -> subscriptionServiceImpl.createSubscription(testUserId, PlanEnum.BASICO));
//    }
//
//    @Test
//    void createSubscription_UserAlreadyHasActiveSubscription() {
//        // Arrange
//        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
//        when(subscriptionRepository.existsByUserIdAndStatus(testUserId, SubscriptionStatusEnum.ATIVA)).thenReturn(true);
//
//        // Act & Assert
//        assertThrows(RuntimeException.class,
//                () -> subscriptionServiceImpl.createSubscription(testUserId, PlanEnum.BASICO));
//    }
//
//    @Test
//    void cancelSubscription_Success() {
//        // Arrange
//        when(subscriptionRepository.findById(testSubscriptionId)).thenReturn(Optional.of(testSubscription));
//        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);
//
//        // Act
//        Subscription result = subscriptionServiceImpl.cancelSubscription(testSubscriptionId);
//
//        // Assert
//        assertNotNull(result);
//        verify(subscriptionRepository).save(any(Subscription.class));
//    }
//
//    @Test
//    void findById_Success() {
//        // Arrange
//        when(subscriptionRepository.findById(testSubscriptionId)).thenReturn(Optional.of(testSubscription));
//
//        // Act
//        Optional<Subscription> result = subscriptionServiceImpl.getSubscriptionById(testSubscriptionId);
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(testSubscriptionId, result.get().getSubscriptionId());
//    }
//}
