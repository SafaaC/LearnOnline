package com.online.learning.course_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.learning.course_service.config.SecurityConfig;
import com.online.learning.course_service.model.Course;
import com.online.learning.course_service.service.CourseService;
import com.online.learning.course_service.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@Import(SecurityConfig.class)
class CourseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CourseService courseService;
    @MockitoBean private JwtService jwtService;
    // @MockitoBean private RegistrationService registrationService;   // only if the controller still injects it

    private Course course(Long id, String title, double price) {
        Course c = new Course();
        c.setId(id);
        c.setTitle(title);
        c.setDescription("desc");
        c.setPrice(price);
        return c;
    }

    @Test
    void listCourses_isPublic_returnsJsonList() throws Exception {
        when(courseService.getAllCourses()).thenReturn(List.of(course(1L, "Java Pro", 499.0)));

        mockMvc.perform(get("/courses"))                         // no user: GET is public in your SecurityConfig
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Java Pro"))
                .andExpect(jsonPath("$[0].price").value(499.0));
    }

    @Test
    void getCourse_found_returns200() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course(1L, "Java Pro", 499.0)));

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCourse_missing_returns404() throws Exception {
        when(courseService.getCourseById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/courses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCourse_asAdmin_bindsBodyAndReturns200() throws Exception {
        when(courseService.addCourse(any(Course.class))).thenReturn(course(5L, "Java Pro", 499.0));
        Course input = course(null, "Java Pro", 499.0);

        mockMvc.perform(post("/courses/admin/create")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseService).addCourse(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Java Pro");     // the JSON body was bound correctly
        assertThat(captor.getValue().getPrice()).isEqualTo(499.0);
    }

    @Test
    void createCourse_asNormalUser_isForbidden() throws Exception {
        mockMvc.perform(post("/courses/admin/create")
                        .with(user("bob").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"price\":0}"))
                .andExpect(status().isForbidden());

        verify(courseService, never()).addCourse(any());
    }

    @Test
    void createCourse_anonymous_isForbidden() throws Exception {
        mockMvc.perform(post("/courses/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"price\":0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCourse_asAdmin_existing_returns204() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course(1L, "Java Pro", 499.0)));

        mockMvc.perform(delete("/courses/admin/delete/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(courseService).deleteCourse(1L);
    }

    @Test
    void deleteCourse_asAdmin_missing_returns404() throws Exception {
        when(courseService.getCourseById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/courses/admin/delete/99").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());

        verify(courseService, never()).deleteCourse(any());
    }
}