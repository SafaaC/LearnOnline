package com.online.learning.notification_service.listener;
import com.online.learning.notification_service.event.RegistrationConfirmedEvent;
import com.online.learning.notification_service.client.UserClient;
import com.online.learning.notification_service.service.EmailService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import feign.Request;
import feign.RequestTemplate;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class RegistrationListenerTest {

    @Mock private  EmailService emailService;
    @Mock private  UserClient userClient;

    @InjectMocks private RegistrationListener registrationListener;

   
   private final RegistrationConfirmedEvent event = new RegistrationConfirmedEvent("bob", 3L);

    @Test
    void userWithEmail_getsWelcomeEmail() {
        when(userClient.getEmail("bob")).thenReturn("bob@example.com");

        registrationListener.onRegistration(event);

        verify(emailService).sendWelcome("bob@example.com", "bob", 3L);
    }

    @Test
    void userWithoutEmail_isSkipped_andNoExceptionThrown() {
        when(userClient.getEmail("bob")).thenThrow(notFound());

        registrationListener.onRegistration(event);            // must NOT throw (retrying could never help)

        verify(emailService, never()).sendWelcome(anyString(), anyString(), anyLong());
    }

    @Test
    void userServiceDown_throws_soKafkaRetries() {
        when(userClient.getEmail("bob")).thenThrow(unavailable());

        assertThatThrownBy(() -> registrationListener.onRegistration(event))
                .isInstanceOf(FeignException.class);   // exception = offset not committed = retry
    }

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