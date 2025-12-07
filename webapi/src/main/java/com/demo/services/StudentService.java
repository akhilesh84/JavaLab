package com.demo.services;

import com.demo.model.Student;

import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student createStudent(Student student);

    Student updateStudent(Student student);

    void deleteStudent(Long id);
}

