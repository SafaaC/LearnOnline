package com.online.learning.payment_service.controller;

import com.online.learning.payment_service.model.Payment;
import com.online.learning.payment_service.model.PaymentRequest;
import com.online.learning.payment_service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        // Initialize test data
        paymentRequest = new PaymentRequest(1000L, "user123");
        payment = new Payment("pay_12345", "Pending", 1000.0, "user123");
        payment.setId(1L);
    }

    @Test
    void testCreatePayment() throws Exception {
        // Mock the service behavior
        when(paymentService.createPaymentOrder(any(PaymentRequest.class))).thenReturn(payment);

        // Perform the POST request
        mockMvc.perform(post("/payment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "amount": 1000,
                                    "userId": "user123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paymentId").value("pay_12345"))
                .andExpect(jsonPath("$.status").value("Pending"))
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.userId").value("user123"));

        // Verify the interaction with the mocked service
        verify(paymentService, times(1)).createPaymentOrder(any(PaymentRequest.class));
    }

    @Test
    void testGetPaymentById() throws Exception {
        // Mock the service behavior
        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        // Perform the GET request
        mockMvc.perform(get("/payment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paymentId").value("pay_12345"))
                .andExpect(jsonPath("$.status").value("Pending"))
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.userId").value("user123"));

        // Verify the interaction with the mocked service
        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    void testUpdatePaymentStatus() throws Exception {
        // Mock the service behavior
        Payment updatedPayment = new Payment("pay_12345", "Success", 1000.0, "user123");
        updatedPayment.setId(1L);

        when(paymentService.updatePaymentStatus("pay_12345", "Success")).thenReturn(updatedPayment);

        // Perform the POST request
        mockMvc.perform(post("/payment/update-status/pay_12345")
                        .param("status", "Success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paymentId").value("pay_12345"))
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.userId").value("user123"));

        // Verify the interaction with the mocked service
        verify(paymentService, times(1)).updatePaymentStatus("pay_12345", "Success");
    }
}
