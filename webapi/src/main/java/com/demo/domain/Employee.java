package com.demo.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Employee {
    private final UUID id;
    private final String name;
    private final Position position;

    private Employee(UUID id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public static Employee create(UUID id, String name, Position position) {
        return new Employee(id, name, position);
    }

    public enum Position {
        MANAGER,
        DEVELOPER,
        DESIGNER,
        QA,
        HR
    }
}
