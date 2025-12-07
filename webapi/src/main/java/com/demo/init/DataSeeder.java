// java
package com.demo.init;

import com.demo.model.Student;
import com.demo.repository.StudentJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

// We want the seeding to happen only in test profile
@Profile("test")
@Component
public class DataSeeder implements CommandLineRunner {

    private final StudentJpaRepository studentJpaRepository;

    public DataSeeder(StudentJpaRepository studentJpaRepository) {
        this.studentJpaRepository = studentJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // safe clear (DB is new on startup when using create-drop)
        studentJpaRepository.deleteAll();

        List<Student> students = List.of(
                new Student("Akhilesh", "Yadav", "akhilesh@example.com", LocalDate.of(1984, 4, 12), 10),
                new Student("Arindam", "Upadhyay", "priya@example.com", LocalDate.of(2002, 8, 3), 9),
                new Student("Animesh", "Rajurkar", "ravi@example.com", LocalDate.of(1983, 1, 22), 10),
                new Student("Nayan", "Tripathi", "neha@example.com", LocalDate.of(2003, 6, 30), 8)
        );

        studentJpaRepository.saveAll(students);

        System.out.println("Seeded " + studentJpaRepository.count() + " students.");
    }
}
