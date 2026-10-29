package com.online.learning.registration_service.controller;

import com.online.learning.registration_service.config.SecurityConfig;
import com.online.learning.registration_service.model.Registration;
import com.online.learning.registration_service.service.JwtService;
import com.online.learning.registration_service.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
class RegistrationControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private RegistrationService registrationService;
    @MockitoBean private JwtService jwtService;

    @Test
    @WithMockUser(username = "bob")
    void register_success_returns200_withRegistrationJson() throws Exception {
        when(registrationService.registerUserForCourse("bob", 1L))
                .thenReturn(new Registration("bob", 1L));

        mockMvc.perform(post("/registrations/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.courseId").value(1));

        verify(registrationService).registerUserForCourse("bob", 1L);
    }

    @Test
    @WithMockUser(username = "bob")
    void register_duplicate_returns409_withMessage() throws Exception {
        when(registrationService.registerUserForCourse("bob", 1L))
                .thenThrow(new IllegalStateException("User already registered for this course"));

        mockMvc.perform(post("/registrations/1"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already registered for this course"));
    }

    @Test
    @WithMockUser(username = "bob")
    void register_paymentMissing_returns409() throws Exception {
        when(registrationService.registerUserForCourse("bob", 2L))
                .thenThrow(new IllegalStateException("Payment not completed for this course"));

        mockMvc.perform(post("/registrations/2"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Payment not completed for this course"));
    }

    @Test
    @WithMockUser(username = "bob")
    void register_paymentServiceDown_returns503() throws Exception {
        when(registrationService.registerUserForCourse("bob", 2L))
                .thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Payment service unavailable"));

        mockMvc.perform(post("/registrations/2"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void register_withoutLogin_isForbidden_andServiceNeverCalled() throws Exception {
        mockMvc.perform(post("/registrations/1"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(registrationService);
    }

    @Test
    void register_withValidJwt_isAccepted() throws Exception {
        when(jwtService.validateToken("good-token")).thenReturn(true);
        when(jwtService.extractUsername("good-token")).thenReturn("bob");
        when(jwtService.extractRoles("good-token")).thenReturn(List.of("ROLE_USER"));
        when(registrationService.registerUserForCourse("bob", 1L))
                .thenReturn(new Registration("bob", 1L));

        mockMvc.perform(post("/registrations/1").header("Authorization", "Bearer good-token"))
                .andExpect(status().isOk());
    }

    @Test
    void register_withBadJwt_isForbidden() throws Exception {
        mockMvc.perform(post("/registrations/1").header("Authorization", "Bearer bad-token"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(registrationService);
    }

    @Test
    @WithMockUser(username = "bob")
    void register_nonNumericCourseId_returns400() throws Exception {
        mockMvc.perform(post("/registrations/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "bob")
    void register_withGet_returns405() throws Exception {
        mockMvc.perform(get("/registrations/1"))
                .andExpect(status().isMethodNotAllowed());
    }
}