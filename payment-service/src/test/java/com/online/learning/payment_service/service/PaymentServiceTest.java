package com.online.learning.payment_service.service;

import com.online.learning.payment_service.config.RazorpayConfig;
import com.online.learning.payment_service.repository.PaymentRepository;
import com.online.learning.payment_service.model.Payment;
import com.online.learning.payment_service.model.PaymentRequest;
import com.razorpay.Order;
import com.razorpay.OrderClient;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RazorpayConfig razorpayConfig;

    @Mock
    private RazorpayClient razorpayClient;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock RazorpayConfig
        when(razorpayConfig.getKey()).thenReturn("test_key");
        when(razorpayConfig.getSecret()).thenReturn("test_secret");

        // Create a spy of the service that returns our mocked RazorpayClient
        paymentService = spy(new PaymentService(paymentRepository, razorpayConfig));
        
        // Use reflection to set the razorpayClient field
        java.lang.reflect.Field razorpayClientField = PaymentService.class.getDeclaredField("razorpayClient");
        razorpayClientField.setAccessible(true);
        razorpayClientField.set(paymentService, razorpayClient);
    }

    @Test
void testCreatePaymentOrder() throws Exception {
    // Prepare test data
    PaymentRequest paymentRequest = new PaymentRequest(100L, "user123");

    // Mock the Orders field in RazorpayClient
    Order mockOrder = mock(Order.class);
    when(mockOrder.get("id")).thenReturn("order123");

    OrderClient mockOrderClient = mock(OrderClient.class);
    when(mockOrderClient.create(any(JSONObject.class))).thenReturn(mockOrder);

    // Use reflection to inject the mocked OrderClient into RazorpayClient
    java.lang.reflect.Field ordersField = RazorpayClient.class.getDeclaredField("Orders");
    ordersField.setAccessible(true);
    ordersField.set(razorpayClient, mockOrderClient);

    // Mock repository save
    Payment mockPayment = new Payment();
    mockPayment.setPaymentId("order123");
    mockPayment.setStatus("Pending");
    mockPayment.setAmount(100.0);
    mockPayment.setUserId("user123");
    when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

    // Call the service method
    Payment createdPayment = paymentService.createPaymentOrder(paymentRequest);

    // Assertions
    assertNotNull(createdPayment);
    assertEquals("order123", createdPayment.getPaymentId());
    assertEquals("Pending", createdPayment.getStatus());
    assertEquals(100.0, createdPayment.getAmount());
    assertEquals("user123", createdPayment.getUserId());

    // Verify repository interactions
    verify(paymentRepository, times(1)).save(any(Payment.class));
}


    @Test
    void testGetPaymentById() {
        // Prepare test data
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentId("order123");
        payment.setStatus("Completed");

        // Mock repository findById
        when(paymentRepository.findById(paymentId)).thenReturn(java.util.Optional.of(payment));

        // Call the service method
        Payment foundPayment = paymentService.getPaymentById(paymentId);

        // Assertions
        assertNotNull(foundPayment);
        assertEquals(paymentId, foundPayment.getId());
        assertEquals("order123", foundPayment.getPaymentId());
        assertEquals("Completed", foundPayment.getStatus());

        // Verify repository interactions
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentById_NotFound() {
        // Prepare test data
        Long paymentId = 1L;

        // Mock repository findById to return empty
        when(paymentRepository.findById(paymentId)).thenReturn(java.util.Optional.empty());

        // Assert that the correct exception is thrown
        assertThrows(RuntimeException.class, () -> paymentService.getPaymentById(paymentId));

        // Verify repository interactions
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testUpdatePaymentStatus() {
        // Prepare test data
        String paymentId = "order123";
        String newStatus = "Completed";
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setStatus("Pending");

        // Mock repository operations
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Call the service method
        Payment updatedPayment = paymentService.updatePaymentStatus(paymentId, newStatus);

        // Assertions
        assertNotNull(updatedPayment);
        assertEquals(newStatus, updatedPayment.getStatus());

        // Verify repository interactions
        verify(paymentRepository, times(1)).findByPaymentId(paymentId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}