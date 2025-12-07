package com.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subjects", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    private String name;

    private Subject(String subjectName){
        this.name = subjectName;
    }

    public static Subject create(String subjectName){
        return new Subject(subjectName);
    }
}
