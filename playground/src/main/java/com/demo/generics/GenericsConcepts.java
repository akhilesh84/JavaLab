package com.demo.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import  java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;
import lombok.Getter;

@Concept
public class GenericsConcepts {
    @Fixture(description = """
            Covariance and Contravariance in Java Generics:
            - Covariance allows a method to accept a more derived type than originally specified. This is
              typically achieved using wildcards with the `extends` keyword.
            - Contravariance allows a method to accept a less derived type than originally specified. This is
              typically achieved using wildcards with the `super` keyword.
            - Invariance means that a generic type is neither covariant nor contravariant.
            """)
    public void covarianceAndContravariance() {
        List<Fruit> fruits = new ArrayList<>();
        List<Apple> apples = List.of(new Apple(), new GrannySmith());

        copyFruitsCovariant(apples, fruits);

        for(Fruit fruit : fruits) {
            System.out.println(fruit.getClass().getSimpleName());
        }
    }

    public void pecsPrinciple() {
        List<Object> objects = new ArrayList<>();
        objects.add("One");
        objects.add(2);

        List<? super Integer> integers = new ArrayList<>(); // OR List<? super Integer> integers = objects;
        integers.add(3); // This will work

        // This will cause a compile-time error. Keep in mind that `? super Integer` means the list can contain
        // instance of any type that is a super type of Integer The key point here is IT CAN CONTAIN, NOT WHAT WE CAN
        // SAFELY ADD TO THE LIST. The hierarchy of inheritance is as below:
        //                         Object
        //                            |
        //                        Number
        //                            |
        //                        Integer
        // Therefore, we can safely add Integer and its subtypes (if any) to the list.
        // However, adding a supertype like Double or Float is not safe because the actual list might be of type List<Integer>.
        // Uncommenting the line below will result in a compile error.

//        integers.add(2.718);

        List<? extends Apple> apples = new ArrayList<>();
        apples.add(null); // This will work

        // This will cause a compile-time error. We cannot add any specific type of Apple or its subtypes
        // because the actual list might be of a more specific type, like List<GrannySmith>.
        // Uncommenting the line below will result in a compile error.
//        apples.add(new GrannySmith());

        // Remember, in JAVA, generics work by type erasure. This means that the generic type information is not
        // available at runtime. Therefore, the compiler cannot determine the exact type of the list at runtime.
        // It only knows that it is some subtype of Apple. Hence, to maintain type safety, it disallows adding any
        // specific type of Apple or its subtypes. It doesn't know, how to cast the object being added to the list.
        // For example, if we had a type, say IndianApple derieved from Apple, and we tried to add an instance of
        // GrannySmith, type erasure system would not be able to determine if this is a valid operation or not.
        // i.e., at teh time of reading it back, it would not know if the object is of type GrannySmith or IndianApple.
        // Therefore, to avoid such potential type safety issues, the compiler disallows adding any specific type of
        // Apple or its subtypes to a list defined with `? extends Apple`.

        // Fix to the above problem is to use `null` which is a valid value for any reference type or bound the type to
        // Apple. i.e., `List<Apple> apples = new ArrayList<>();`. In that case, we can add any type of Apple or
        // its subtypes.
    }

    @Fixture(description = """
            Counting Matches in a List Using Generics and Predicates:
            - This method demonstrates how to count elements in a list that match a given condition using
              generics and the Predicate functional interface.
            - The method is generic and can work with any type of list, as long as a suitable predicate is provided.
            """)
    public void testTypeBounds() {
        List<String> strings = List.of("apple", "banana", "apricot", "cherry", "avocado");
        long count = countMatches(strings, s -> s.startsWith("a"));
        System.out.println("Number of strings starting with 'a': " + count);

        List<Integer> numbers = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
        count = countMatches(numbers, s -> s%4 == 0);
        System.out.println("Number of elements divisible by 4: " + count);
    }

    @Fixture(description = """
            
            """)
    public void pecsPrinciple1()
    {
        List<Number> numbers = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        List<Integer> integers = IntStream.rangeClosed(11, 20).boxed().toList();

        copy(integers, numbers);
        System.out.println("Numbers after copy: " + numbers);

//        copy(numbers, integers); // This will cause a compile-time error
//        reason: inference variable T has incompatible bounds
//        upper bounds: java.lang.Integer,java.lang.Object
//        lower bounds: java.lang.Number
    }

    @Fixture(description = """
            Compilation by erasure means that at run time parameterized types are replaced by the corresponding raw
            type: for example, the interfaces List<Integer>, List<String>, and List<List<String>> are all implemented
            by a single interface, namely List.
            
            One consequence is that static members of a generic class are shared across all instantiations of that
            class, including instantiations at different types. Static members of a class cannot refer to the type
            parameter of a generic class, and when accessing a static member the class name should not be parameterized.
            """)
    public void staticMembersOfGenericClasses() {

        class Cell<T> {
            private final int id;
            private final T value;
            private final static AtomicInteger count = new AtomicInteger();
            private static int nextId() { return count.getAndIncrement(); }
            public Cell(T value) {
                this.value = value;
                id = nextId();
            }
            public T getValue() { return value; }
            public int getId() { return id; }
            public static int getCount() { return count.get(); }
        }

        Cell<String> a = new Cell<String>("one");
        Cell<Integer> b = new Cell<Integer>(2);

        System.out.println("Cell a: id=" + a.getId() + ", value=" + a.getValue());
        System.out.println("Cell b: id=" + b.getId() + ", value=" + b.getValue());
        System.out.println("Total Cell instances created: " + Cell.getCount());
    }

    @Fixture(description = """
            Principle of Truth in Advertising:
            - This principle highlights the potential pitfalls of type erasure in Java generics.
            - It demonstrates that even if a method signature suggests a certain type, the actual runtime type
              may differ, leading to exceptions like ClassCastException.
            - The Principle of Truth in Advertising in Java generics refers to the rule that “the reified type of an
            array must be a subtype of the erasure of its static type.” This principle is about ensuring the runtime
            type of an array (which is fully known and “reified”) aligns correctly with the compile-time type (the
            static type’s erased form) to avoid type errors and class cast exceptions.
              In simpler terms, when Java code advertises (i.e., declares) that it will return an array of a certain
              type, the actual array created and returned at runtime must truly be of that type or its subtype. If the
              runtime type of the array is not a subtype of the declared (erased) static type, Java will throw a
              ClassCastException since arrays in Java carry runtime type information.
            """)
    public void PrincipleOfTruthInAdvertising()
    {
        List<String> strings = Arrays.asList("one", "two");
        try {
            String[] a = toArray(strings);  // ClassCastException here!
        }
        catch (ClassCastException e) {
            System.out.println("Caught expected ClassCastException: " + e.getMessage());
        }

        System.out.println("Below is the output following the right technique. Check out the code in the fixture.");

        String[] a = toArray(strings, new String[0]);
        System.out.println(Arrays.toString(a));  // [one, two]
    }

    private <T> long countMatches(List<T> source, Predicate<T> predicate) {
//        return source.stream().filter(predicate).count();

        long count = 0;
        for (T item : source) {
            if (predicate.test(item)) count++;
        }
        return count;
    }

    private <T> void reverseList(List<T> list) {
        int left = 0;
        int right = list.size() - 1;
        while (left < right) {
            T temp = list.get(left);
            list.set(left, list.get(right));
            list.set(right, temp);
            left++;
            right--;
        }
    }

    private static <T> T[] toArray(Collection<T> c) {
        T[] a = (T[]) new Object[c.size()];  // unchecked cast
        int i = 0;
        for (T x : c)
            a[i++] = x;
        return a;
    }

    static <T> T[] toArray(Collection<T> c, T[] a) {
        if (a.length < c.size()) a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), c.size());

        int i = 0;

        for (T x : c) a[i++] = x;

        if (i < a.length) a[i] = null;

        return a;
    }

    /*
    * The best way to understand covariance and contravariance is through the PECS mnemonic:
    * "Producer Extends, Consumer Super".
    * - If a structure produces objects of type T, you should use `? extends T` (covariance).
    * - If a structure consumes objects of type T, you should use `? super T` (contravariance).
    * - If a structure both produces and consumes objects of type T, you should use T (invariance).
    *
    * In the method below, `source` is a producer of `Fruit` objects, so we use `? extends Fruit`.
    * `destination` is a consumer of `Fruit` objects, so we use `? super Fruit`.
    * */
    private void copyFruitsCovariant(Iterable<? extends Fruit> source, List<? super Fruit> destination) {
        for (Fruit fruit : source) {
            destination.add(fruit);
        }
    }

    private <T> void copy(List<? extends T> source, List<? super T> destination) {
        destination.addAll(source);
    }

    private class Fruit {}
    private class Apple extends Fruit {
        public Apple() {}
    }
    private class GrannySmith extends Apple {
        public GrannySmith() {
            super();
        }
    }
}
