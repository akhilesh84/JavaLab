package com.demo.concurrency;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

@Concept
public class BasicThreadsDemo
{
    @Fixture(description = "Demonstrates basic thread creation and execution.")
    public void basic_thread_execution() {
        Thread thread = new Thread(() -> {
            System.out.println("Hello from a thread!" + Thread.currentThread().getName());
        });
        thread.start();

        try {
            thread.join(); // Wait for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread has finished execution.");
    }

//    @Fixture(description = "Demonstrates creating multiple threads and managing them.")
    public void thread_limit_test(){
        var threadCount = new AtomicInteger(0);
        try {
            while (true) {
                var thread = new Thread(() -> {
                    threadCount.incrementAndGet();
                    LockSupport.park();
                });
                thread.start();
            }
        } catch (OutOfMemoryError error) {
            System.out.println("Reached thread limit: " + threadCount);
            error.printStackTrace();
        }
    }
}
