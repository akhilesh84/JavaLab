package com.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;
import java.util.stream.IntStream;

import com.demo.domain.Employee;

@RestController
@RequestMapping("/api/employee")
class EmployeeController {

    @GetMapping("/all")
    public Iterable<Employee> getEmployees() {
        return fetchEmployeesFromDatabase();
    }

    private Iterable<Employee> fetchEmployeesFromDatabase() {
        return IntStream.range(0, 1000).mapToObj(i -> Employee.create(UUID.randomUUID(), getRandomName(), getRandomPosition()))
                .toList();
    }

    private String getRandomName() {
        String[] names = {"Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack"};
        return names[(int) (Math.random() * names.length)];
    }

    private Employee.Position getRandomPosition() {
        Employee.Position[] positions = Employee.Position.values();
        return positions[(int) (Math.random() * positions.length)];
    }
}
