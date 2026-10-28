package com.online.learning.registration_service.service;

import com.online.learning.registration_service.client.CourseClient;
import com.online.learning.registration_service.client.CourseDto;
import com.online.learning.registration_service.client.PaymentClient;
import com.online.learning.registration_service.event.RegistrationConfirmedEvent;
import com.online.learning.registration_service.model.Registration;
import com.online.learning.registration_service.repository.RegistrationRepository;
import feign.FeignException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.server.ResponseStatusException;
import java.util.concurrent.CompletableFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong; 
import static org.mockito.ArgumentMatchers.anyString;   
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import feign.Request;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;


@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    
    @Mock private RegistrationRepository registrationRepository;
    @Mock private CourseClient courseClient;
    @Mock private PaymentClient paymentClient;
    @Mock private KafkaTemplate<String, RegistrationConfirmedEvent> kafkaTemplate;


    @InjectMocks private RegistrationService registrationService;
    
    @BeforeEach
    void setUp() {
        // default behaviour for every test: save returns what it was given,
        // and Kafka's send returns a future that never completes (so the callback doesn't run)
        lenient().when(registrationRepository.save(any(Registration.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        lenient().when(kafkaTemplate.send(anyString(), anyString(), any(RegistrationConfirmedEvent.class)))
                .thenReturn(new CompletableFuture<>());
    }

    @Test
    void freeCourse_registersWithoutCallingPayment() {
        // Arrange
       when(courseClient.getCourse(1L)).thenReturn(new CourseDto(1L, "Free Course", 0.0));

        // Act
        Registration result= registrationService.registerUserForCourse("bob", 1L);
        // Assert                   
        assertThat(result.getUsername()).isEqualTo("bob");
        verify(paymentClient, never()).verify(anyString(), anyLong());  
        verify(kafkaTemplate).send(eq("registration-events"), eq("bob"),
                any(RegistrationConfirmedEvent.class));
    }
    
    @Test 
    void paidCourse_withpayment_registers(){
        // Arrange
        when(courseClient.getCourse(2L)).thenReturn(new CourseDto(2L, "Paid Course", 100.0));
        when(paymentClient.verify("bob", 2L)).thenReturn(true);
        // Act
        Registration result = registrationService.registerUserForCourse("bob", 2L);
        // Assert
        assertThat(result.getCourseId()).isEqualTo(2L);
        verify( registrationRepository).save(any(Registration.class));
    }

    @Test
    void paidCourse_withoutPayment_throws() {
        // Arrange
        when(courseClient.getCourse(3L)).thenReturn(new CourseDto(3L, "Paid Course", 50.0));
        when(paymentClient.verify("bob", 3L)).thenReturn(false);
        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUserForCourse("bob", 3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payment not completed");
        verify(registrationRepository, never()).save(any(Registration.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(RegistrationConfirmedEvent.class));
    }   

    @Test 
    void alreadyRegistered_throws() {
        // Arrange
        when(courseClient.getCourse(4L)).thenReturn(new CourseDto(4L, "Some Course", 0.0));
        when(registrationRepository.existsByUsernameAndCourseId("bob", 4L)).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUserForCourse("bob", 4L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already registered");
        verify(paymentClient, never()).verify(anyString(), anyLong());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(RegistrationConfirmedEvent.class));
    }

    @Test 
    void unknownCourse_throws() {
        // Arrange
        when(courseClient.getCourse(5L)).thenThrow(notFound());
        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUserForCourse("bob", 5L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Course not found");
        verify(paymentClient, never()).verify(anyString(), anyLong());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(RegistrationConfirmedEvent.class));
    }

    @Test
    void courseServiceUnavailable_throws() {
        // Arrange
        when(courseClient.getCourse(6L)).thenThrow(unavailable());
        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUserForCourse("bob", 6L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Course service unavailable");
        verify(paymentClient, never()).verify(anyString(), anyLong());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(RegistrationConfirmedEvent.class));
    }
    @Test 
    void paymentServiceUnavailable_throws() {
        // Arrange
        when(courseClient.getCourse(7L)).thenReturn(new CourseDto(7L, "Paid Course", 20.0));
        when(paymentClient.verify("bob", 7L)).thenThrow(unavailable());
        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUserForCourse("bob", 7L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Payment service unavailable");
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(RegistrationConfirmedEvent.class));
        verify (registrationRepository, never()).save(any(Registration.class));
    }

    @Test 
    void kafkaFailure_doesNotPreventRegistration() {
        // Arrange
        when(courseClient.getCourse(8L)).thenReturn(new CourseDto(8L, "Free Course", 0.0));
        doAnswer(inv -> {
            CompletableFuture<SendResult<String, RegistrationConfirmedEvent>> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Kafka down"));
            return future;
        }).when(kafkaTemplate).send(anyString(), anyString(), any(RegistrationConfirmedEvent.class));
        // Act
        Registration result = registrationService.registerUserForCourse("bob", 8L);
        // Assert
        assertThat(result.getUsername()).isEqualTo("bob");
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test 
    void kafkaSuccess_logsSuccess() {
        // Arrange
        when(courseClient.getCourse(9L)).thenReturn(new CourseDto(9L, "Free Course", 0.0));
        CompletableFuture<SendResult<String, RegistrationConfirmedEvent>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>("registration-events", "bob", new RegistrationConfirmedEvent("bob", 9L)), null));
        when(kafkaTemplate.send(anyString(), anyString(), any(RegistrationConfirmedEvent.class))).thenReturn(future);
        // Act
        Registration result = registrationService.registerUserForCourse("bob", 9L);
        // Assert
        assertThat(result.getUsername()).isEqualTo("bob");
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test 
    void kafkaFailure_logsFailure() {
        // Arrange
        when(courseClient.getCourse(10L)).thenReturn(new CourseDto(10L, "Free Course", 0.0));
        CompletableFuture<SendResult<String, RegistrationConfirmedEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), any(RegistrationConfirmedEvent.class))).thenReturn(future);
        // Act
        Registration result = registrationService.registerUserForCourse("bob", 10L);
        // Assert
        assertThat(result.getUsername()).isEqualTo("bob");
        verify(registrationRepository).save(any(Registration.class));
    }

    // ---- helpers to build the exceptions Feign would throw ----
    private static Request request() {
        return Request.create(Request.HttpMethod.GET, "/x", Map.of(), null,
                StandardCharsets.UTF_8, new RequestTemplate());
    }
    private static FeignException.NotFound notFound() {
        return new FeignException.NotFound("not found", request(), null, Map.of());
    }
    private static FeignException.ServiceUnavailable unavailable() {
        return new FeignException.ServiceUnavailable("down", request(), null, Map.of());
    }

    
}
