package com.demo.services;

import java.util.List;

import com.demo.model.Student;
import com.demo.repository.StudentJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentJpaRepository studentJpaRepository;

    public StudentServiceImpl(StudentJpaRepository studentJpaRepository) {
        this.studentJpaRepository = studentJpaRepository;
    }

    @Override
    @Transactional
    public List<Student> getAllStudents() {
        return studentJpaRepository.findAll();
    }

    @Override
    @Transactional
    public Student getStudentById(Long id) {
        return studentJpaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Student createStudent(Student student) {
        return studentJpaRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudent(Student student) {
        if (studentJpaRepository.existsById(student.getId())) {
            return studentJpaRepository.save(student);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        studentJpaRepository.deleteById(id);
    }
}
