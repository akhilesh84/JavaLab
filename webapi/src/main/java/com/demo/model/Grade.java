package com.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Getter
    private String semester;

    // On the cardinality attribute of the ManyToOne relationship:
    // The 'cascade = {CascadeType.MERGE}' setting means that when a Grade entity is merged (updated),
    // any changes to the associated Subject entity will also be merged.
    // This is useful when you want to ensure that updates to the Subject are propagated
    // when updating a Grade, without affecting other operations like persist or remove.
    // Also, existing/managed Subject instances are merged instead of re-persisted
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Getter
    @Setter
    private int score;

}
