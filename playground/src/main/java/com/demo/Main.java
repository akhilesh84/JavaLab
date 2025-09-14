package com.demo;

import com.demo.shell.ConceptREPL;
import org.reflections.Reflections;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        /*
        Reflections reflections = new Reflections("com.demo");
        reflections.getTypesAnnotatedWith(Concept.class)
                .forEach(c -> {
            try {
                Class<?> clazz = Class.forName(c.getName());
                var obj = c.getDeclaredConstructor().newInstance();
                Method[] methods = getAnnotatedMethods(c, Fixture.class);

                System.out.println("Running fixtures in class: " + clazz.getSimpleName());
                System.out.println("=========================");

                for (Method method : methods) {
                    Fixture fixture = method.getAnnotation(Fixture.class);
                    System.out.println("Fixture: " + method.getName() + ", Description: " + fixture.description() + "\n");
                    method.invoke(obj);
                }

                System.out.println("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        */

        ConceptREPL repl = new ConceptREPL();
        repl.start();
    }

    private static Method[] getAnnotatedMethods(Class<?> clazz, Class<Fixture> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .toArray(Method[]::new);
    }
}