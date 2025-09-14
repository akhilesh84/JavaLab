package com.demo.concurrency;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;

@Concept
public class ExecutorsDemo {
    @Fixture(description = "Demonstrates the use of Executors in Java Concurrency")
    public void basic() {
        try(ExecutorService executor = Executors.newFixedThreadPool(10)) {
            IntStream.range(0, 5).forEach(i -> {
                executor.submit(this::DoSomething);
            });
        }
    }

    private void DoSomething() {
        try {
            Thread.sleep(Duration.ofMillis(new Random().nextLong(100, 500)));
            System.out.println("Current Thread: " + Thread.currentThread().getName());
        }
        catch (InterruptedException e) {}
    }
}
