package com.demo.services;

import java.util.List;

import com.demo.model.Student;
import com.demo.repository.StudentJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentJpaRepository studentJpaRepository;

    public StudentServiceImpl(StudentJpaRepository studentJpaRepository) {
        this.studentJpaRepository = studentJpaRepository;
    }

    public List<Student> getAllStudents() {
        return studentJpaRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentJpaRepository.findById(id).orElse(null);
    }

    public Student createStudent(Student student) {
        return studentJpaRepository.save(student);
    }

    public Student updateStudent(Student student) {
        if (studentJpaRepository.existsById(student.getId())) {
            return studentJpaRepository.save(student);
        }
        return null;
    }
}
