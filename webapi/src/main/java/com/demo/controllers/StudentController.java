package com.demo.controllers;

import com.demo.model.Student;
import com.demo.services.StudentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public Student createStudent(@Validated @RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping()
    public Student updateStudent(@Validated @RequestBody Student student) {
        return studentService.updateStudent(student);
    }
}
