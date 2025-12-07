package com.demo.controllers;

import com.demo.model.Student;
import com.demo.services.StudentService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@ComponentScan(basePackages = {"com.demo.services"})
@RestController
@RequestMapping("/api/student")
class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable(value = "id") Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping()
    public ResponseEntity<Student> createStudent(@Validated @RequestBody Student student) {
        Student created = studentService.createStudent(student);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping()
    public Student updateStudent(@Validated @RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentById(@PathVariable(value = "id") Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
