package com.demo.generics;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;
import lombok.Getter;

import java.util.Comparator;
import java.util.stream.IntStream;

@Concept(description = "Understanding comparison and bounds in generics")
public class ComparisonAndBounds {
    @Fixture(description = """
            This method makes use of the Comparable interface to sort a list of Student objects based on their
            scores in three subjects: Math, Physics, and Chemistry.
            However, the limitation of this approach is that it only allows for a single natural ordering of Student
            objects. If we wanted to sort by different criteria (e.g., by name or ID), we would need to implement
            additional comparators.
            """)
    public void demonstrateOrderingofComparableTypes() {
        var students = IntStream.range(0, 10).mapToObj(
                i -> new Student(
                        "Student" + i,
                        i,
                        (int) (Math.random() * 100),
                        (int) (Math.random() * 100),
                        (int) (Math.random() * 100))
        ).toList();

        students.stream().sorted().forEach(System.out::println);

        // Above snippet can be replaced with below snippet to achieve the same result
//        students.stream()
//                .sorted((s1, s2) -> {
//                    if(s1.mathScore == s2.mathScore){
//                        if(s1.physicsScore == s2.physicsScore) return Integer.compare(s1.chemistryScore, s2.chemistryScore);
//                        else return Integer.compare(s1.physicsScore, s2.physicsScore);
//                    }
//                    else return Integer.compare(s1.mathScore, s2.mathScore);
//                })
//                .forEach(System.out::println);
    }

    @Fixture(description = """
            Demonstrates the use of a custom Comparator to sort Student objects by their names in descending order.
            This approach provides flexibility to define multiple sorting criteria without modifying the Student class.
            """)
    public void demonstrateCustomComparator() {
        var students = IntStream.range(0, 10).mapToObj(
                i -> new Student(
                        "Student" + i,
                        i,
                        (int) (Math.random() * 100),
                        (int) (Math.random() * 100),
                        (int) (Math.random() * 100))
        ).toList();

        var customComparator = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return String.valueOf(o2.getName()).compareTo(String.valueOf(o1.getName()));
            }
        };

        students.stream().sorted(customComparator).forEach(System.out::println);

        // Above snippet can be replaced with below snippet to achieve the same result as Comparator is a functional
        // interface
//        students
//                .stream()
//                .sorted((o1, o2) -> String.valueOf(o2.getName()).compareTo(String.valueOf(o1.getName())))
//                .forEach(System.out::println);
    }

    @Getter
    class Student implements Comparable<Student> {
        private final String name;
        private final int id;
        private final int mathScore;
        private final int physicsScore;
        private final int chemistryScore;

        public Student(String name, int id, int mathScore, int physicsScore, int chemistryScore) {
            this.name = name;
            this.id = id;
            this.mathScore = mathScore;
            this.physicsScore = physicsScore;
            this.chemistryScore = chemistryScore;
        }

        @Override
        public int compareTo(Student other) {
            if(this.mathScore == other.mathScore){
                if(this.physicsScore == other.physicsScore) return Integer.compare(this.chemistryScore, other.chemistryScore);
                else return Integer.compare(this.physicsScore, other.physicsScore);
            }
            else return Integer.compare(this.mathScore, other.mathScore);
        }

        @Override
        public String toString() {
            return "Student{" + "name='" + name + '\'' + ", id=" + id + ", mathScore=" + mathScore + ", physicsScore=" + physicsScore + ", chemistryScore=" + chemistryScore + '}';
        }
    }
}

