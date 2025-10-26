package com.demo.reflection;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ReflectionForGenerics {
    public static void toString(Class<?> k) {
        System.out.println(k + " (toString)");
        System.out.println(k);
        for (Field f : k.getDeclaredFields())
            System.out.println(f.toString());
        for (Constructor<?> c : k.getDeclaredConstructors())
            System.out.println(c.toString());
        for (Method m : k.getDeclaredMethods())
            System.out.println(m.toString());
        System.out.println();
    }
    public static void toGenericString(Class<?> k) {
        System.out.println(k + " (toGenericString)");
        System.out.println(k.toGenericString());
        System.out.println("Fields");
        for (Field f : k.getDeclaredFields())
            System.out.println("\t" + f.toGenericString());
        System.out.println("Constructors");
        for (Constructor<?> c : k.getDeclaredConstructors())
            System.out.println("\t" + c.toGenericString());
        System.out.println("Declared Methods");
        for (Method m : k.getDeclaredMethods())
            System.out.println("\t" + m.toGenericString());
        System.out.println();
    }

    public static String printClass(Class<?> c) {
        return "class " +
                printType(c) +
                printTypeParameters(c.getTypeParameters()) +
                printSuperclass(c.getGenericSuperclass()) +
                printInterfaces(c.getGenericInterfaces());
    }

    public static String printSuperclass(Type sup) {
        if (!sup.equals(Object.class)) {
            return " extends " + printType(sup) + "\n";
        }
        return "";
    }

    public static String printInterfaces(Type[] impls) {
        if (impls.length > 0) {
            return "implements " + Arrays.stream(impls)
                    .map(ReflectionForGenerics::printType)
                    .collect(Collectors.joining(",")) + "\n";
        }
        return "";
    }

    public static String printTypeParameters(TypeVariable<?>[] vars) {
        if (vars.length > 0) {
            return Arrays.stream(vars)
                    .map(b -> b.getName() + printBounds(b.getBounds()))
                    .collect(Collectors.joining(",","<",">\n"));
        }
        return "";
    }

    public static String printBounds(Type[] bounds) {
        if (bounds.length > 0 && ! Arrays.equals(bounds,new Type[]{ Object.class })) {
            return " extends " +
                    Arrays.stream(bounds)
                            .map(ReflectionForGenerics::printType)
                            .collect(Collectors.joining(" & "));
        }
        return "";
    }

    public static String printType(Type type) {
        return switch(type) {
            case Class<?> cls -> cls.getName();
            case ParameterizedType p -> printParameterizedType(p);
            case TypeVariable<?> tv -> tv.getName();
            case GenericArrayType gat -> gat.getGenericComponentType() + "[]";
            case WildcardType wt -> printWildcard(wt);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private static String printParameterizedType(ParameterizedType p) {
        Class<?> c = (Class<?>)p.getRawType();
        Type o = p.getOwnerType();
        String ptString = o != null ? printType(o) + "." : "";
        ptString = ptString + c.getName();
        return ptString + Arrays.stream(p.getActualTypeArguments())
                .map(ReflectionForGenerics::printType)
                .collect(Collectors.joining(",","<",">"));
    }

    private static String printWildcard(WildcardType wt) {
        Type[] upper = wt.getUpperBounds();
        Type[] lower = wt.getLowerBounds();
        if (lower.length == 1) {
            return "? super " + printType(lower[0]);
        } else if (upper.length == 1 && ! upper[0].equals(Object.class)) {
            return "? extends" + printType(upper[0]);
        }
        return "";
    }
}
