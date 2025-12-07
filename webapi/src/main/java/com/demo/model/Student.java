// java
package com.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    private Integer grade;

    public Student() { }

    public Student(String firstName, String lastName, String email, LocalDate dateOfBirth, Integer grade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.grade = grade;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public LocalDate getDateOfBirth() {
//        return dateOfBirth;
//    }
//
//    public void setDateOfBirth(LocalDate dateOfBirth) {
//        this.dateOfBirth = dateOfBirth;
//    }
//
//    public Integer getGrade() {
//        return grade;
//    }
//
//    public void setGrade(Integer grade) {
//        this.grade = grade;
//    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", grade=" + grade +
                '}';
    }
}
