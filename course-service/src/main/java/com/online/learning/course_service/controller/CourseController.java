package com.online.learning.course_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.online.learning.course_service.model.Course;
import com.online.learning.course_service.service.CourseService;

import java.util.Optional;


import java.util.List;
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend origin
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // Fetch all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
    Optional<Course> optionalCourse = courseService.getCourseById(id);
    if (optionalCourse.isPresent()) {
        return ResponseEntity.ok(optionalCourse.get()); // Return 200 with the course
    } else {
        return ResponseEntity.notFound().build(); // Return 404 if the course is not found
    }
}
    // Add a new course
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.addCourse(course);
    }

    // Update course details
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        return courseService.getCourseById(id).map(existingCourse -> {
            existingCourse.setTitle(updatedCourse.getTitle());
            existingCourse.setDescription(updatedCourse.getDescription());
            existingCourse.setPrice(updatedCourse.getPrice());
            courseService.addCourse(existingCourse);
            return ResponseEntity.ok(existingCourse);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a course
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCourse(@PathVariable Long id) {
        return courseService.getCourseById(id).map(course -> {
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
