// java
package com.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String firstName;

    @Column(nullable = false)
    @Getter
    @Setter
    private String lastName;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private LocalDate dateOfBirth;

    @Getter
    @Setter
    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Grade> grades = new HashSet<>();

    public void addGrade(Grade grade) {
        grades.add(grade);

        // This is to enable bidirectional linkage from grade to student
        grade.setStudent(this);
    }

    public void removeGrade(Grade grade) {
        grades.remove(grade);
        grade.setStudent(null);
    }

    public Student() { }

    public Student(String firstName, String lastName, String email, LocalDate dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
