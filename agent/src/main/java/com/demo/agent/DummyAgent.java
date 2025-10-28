package com.demo.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;


public class DummyAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("DummyAgent.premain");

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException {

                if ("com/demo/mylib/MyService".equals(className)) {
                    System.out.println("=== Replacing MyService with Byte Buddy ===");

                    try {
                        String classNameDotted = className.replace('/', '.');

                        // Create a composite locator that can find both your class and system classes
                        ClassFileLocator classFileLocator = new ClassFileLocator.Compound(
                                ClassFileLocator.ForClassLoader.ofSystemLoader(),
                                ClassFileLocator.Simple.of(classNameDotted, classfileBuffer)
                        );

                        TypePool typePool = TypePool.Default.of(classFileLocator);
                        TypeDescription typeDescription = typePool.describe(classNameDotted).resolve();

                        byte[] modifiedBytes = new ByteBuddy()
                                .redefine(typeDescription, classFileLocator)
                                .method(ElementMatchers.named("performService"))
                                .intercept(FixedValue.value("Service performed by Byte Buddy replacement!"))
                                .make()
                                .getBytes();

                        System.out.println("Generated modified bytecode: " + modifiedBytes.length + " bytes");
                        return modifiedBytes;

                    } catch (Exception e) {
                        System.err.println("Error creating replacement class: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                return classfileBuffer;
            }
        });
    }
}
