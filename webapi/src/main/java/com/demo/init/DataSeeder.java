// java
package com.demo.init;

import com.demo.model.Grade;
import com.demo.model.Student;
import com.demo.model.Subject;
import com.demo.repository.StudentJpaRepository;
import com.demo.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

// We want the seeding to happen only in test profile
@Profile("test")
@Component
public class DataSeeder implements CommandLineRunner {

    private final StudentJpaRepository studentJpaRepository;
    private final SubjectRepository subjectRepository;

    public DataSeeder(StudentJpaRepository studentJpaRepository, SubjectRepository subjectRepository)
    {
        this.studentJpaRepository = studentJpaRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // safe clear (DB is new on startup when using create-drop)
        studentJpaRepository.deleteAll();

        List<Student> students = List.of(
                new Student("Akhilesh", "Yadav", "akhilesh@example.com", LocalDate.of(1984, 4, 12)),
                new Student("Arindam", "Upadhyay", "priya@example.com", LocalDate.of(2002, 8, 3)),
                new Student("Animesh", "Rajurkar", "ravi@example.com", LocalDate.of(1983, 1, 22)),
                new Student("Nayan", "Tripathi", "neha@example.com", LocalDate.of(2003, 6, 30))
        );

        students.forEach(student -> {
            Subject math = subjectRepository.findByName("Mathematics")
                    .orElseGet(() -> subjectRepository.save(Subject.create("Mathematics")));

            Subject science = subjectRepository.findByName("Science")
                    .orElseGet(() -> subjectRepository.save(Subject.create("Science")));

            Grade grade1 = new Grade();
            grade1.setSubject(math);
            grade1.setScore(85);

            Grade grade2 = new Grade();
            grade2.setSubject(science);
            grade2.setScore(90);

            student.addGrade(grade1);
            student.addGrade(grade2);
        });

        studentJpaRepository.saveAll(students);

        System.out.println("Seeded " + studentJpaRepository.count() + " students.");
    }
}
