package com.demo.reflection;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Map;

@Concept(description = "A demo for Java Reflection API")
public class ReflectionDemo {

    @Fixture(description = "Demonstrate reflection for a sample class")
    public void demonstrateReflection() {
        ReflectionForGenerics.toGenericString(Map.class);
    }

    @Fixture(description = "Demonstrate type inspection")
    public void DemonstrateTypeInspection()
    {
        System.out.println(ReflectionForGenerics.printClass(ArrayList.class));
    }
}
