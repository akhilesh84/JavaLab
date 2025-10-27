package com.demo.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class DummyAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("DummyAgent.premain");

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException {

                if (true /*className.contains("com/demo")*/) {
                    String resourcePath = className.replace('.', '/') + ".class";
                    if (loader != null) {
                        java.net.URL url = loader.getResource(resourcePath);
                        System.out.println("Loaded class: " + className + " from: " + url);
                    } else {
                        System.out.println("Loaded class: " + className + " from Bootstrap ClassLoader");
                    }
                }

                return classfileBuffer;
            }
        });
    }
}
