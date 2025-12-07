package com.demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class StudentJpaRepositoryTests {


    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public StudentJpaRepositoryTests(EntityManagerFactory emf) {
        this.entityManagerFactory = emf;
    }

    @Test
    void contextLoads() {
        assertNotNull(entityManagerFactory, "EntityManagerFactory should be provided by Spring Boot");
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            assertNotNull(em, "EntityManager should be creatable from the factory");
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
