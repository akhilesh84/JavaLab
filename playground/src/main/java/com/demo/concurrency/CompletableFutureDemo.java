package com.demo.concurrency;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;

@Concept
public class CompletableFutureDemo {
    @Fixture(description = "Demonstrates basic usage of CompletableFuture in Java Concurrency.")
    public void basic_completable_future() {
        // A completable future represents the result of an asynchronous computation. i.e., an operation that will
        // complete in the future.
        var future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // Simulate a long-running task
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Hello from CompletableFuture!";
        }).thenApplyAsync(String::toUpperCase);

        System.out.println("Hello from main thread");

        future.join(); // Wait for the future to complete
        System.out.println(future.getNow(null)); // Get the result of the future
    }

    @Fixture(description = "Demonstrates chaining multiple continuations with CompletableFuture.")
    public void futures_continuation_demo(){
        var future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // Simulate a long-running task
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Hello from CompletableFuture!";
        }).thenApplyAsync(String::toUpperCase)
          .thenApplyAsync(s -> s + " - Appended Text")
          .thenAcceptAsync(System.out::println);

        System.out.println("Hello from main thread");

        future.join(); // Wait for the future to complete
    }

    @Fixture(description = """
            Demonstrates advanced usage of CompletableFuture. Basically the idea is to be able to associate a completer
            to a collection of futures, and as each future completes, the completer is invoked. This is useful for
            scenarios where you want to process results as they become available, rather than waiting for all tasks to
            complete.
            """)
    public void asTheyComplete_completable_future() {
        var tasks = IntStream.range(0, 50)
                .mapToObj(i -> CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                Thread.sleep(new Random().nextInt(100,2000)); // Simulate a long-running task
                            } catch (InterruptedException e) {
                                throw new IllegalStateException(e);
                            }
                            return "Result from Task " + i;
                        })
                        .thenApplyAsync(String::toUpperCase)
                        .thenAcceptAsync(System.out::println)
                )
                .toList().toArray(new CompletableFuture[0]);

        CompletableFuture.allOf(tasks)
                .thenRun(() -> System.out.println("All tasks completed"))
                .join();
    }

    @Fixture(description = "Demonstrates basic usage of Virtual Threads in Java Concurrency.")
    public void virtual_threads_demo()
    {
        try(var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 100_000).forEach(i -> {
                executor.execute(() -> {
                    try {
                        Thread.sleep(Duration.ofMillis(50));
                        System.out.println("Completed task " + i + ", Threadpool group name: " + Thread.currentThread().getThreadGroup().getName());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Fixture(description = """
            Demonstrates Little's Law in action by comparing throughput of different thread pool configurations
            (including virtual threads) when executing a large number of I/O-bound tasks.
            Little's Law states that the average number of items in a queuing system (L) is equal to the average arrival
            rate (λ) multiplied by the average time an item spends in the system (W): L = λ * W.
            In this demo, we simulate a scenario where tasks have a fixed average response time and measure how many
            tasks can be completed in a given time frame with different thread pool configurations.
            """)
    public void little_law_demo()
    {
        int numTasks = 10000; // 1
        int avgResponseTimeMillis = 500; // Average task response time // 2
        // Simulate adjustable I/O-bound work
        Runnable ioBoundTask = () -> {
            try {
                Thread.sleep(Duration.ofMillis(avgResponseTimeMillis)); // 3
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        System.out.println("=== Little’s Law Throughput Comparison ===");
        System.out.println("Testing " + numTasks + " tasks with " + avgResponseTimeMillis + "ms latency each\n");
        f_benchmark("Virtual Threads", Executors.newVirtualThreadPerTaskExecutor(), ioBoundTask, numTasks);
        f_benchmark("Fixed ThreadPool (100)", Executors.newFixedThreadPool(100), ioBoundTask, numTasks);
        f_benchmark("Fixed ThreadPool (500)", Executors.newFixedThreadPool(500), ioBoundTask, numTasks);
        f_benchmark("Fixed ThreadPool (1000)", Executors.newFixedThreadPool(1000), ioBoundTask, numTasks);
    }

    @Fixture(description = """
            Demonstrates how virtual threads can be pinned to a specific OS thread when performing blocking operations
            within synchronized blocks. This is important to prevent thread starvation and ensure that other virtual
            threads can continue executing while one is blocked.
            In this demo, we create multiple virtual threads that attempt to acquire a lock on a shared resource.
            One of the threads prints its identity before and after acquiring the lock to show that it remains on the
            same OS thread during the blocking operation.
            """)
    public void virtual_thread_pinning_demo()
    {
        Object lock = new Object();
        List<Thread> threadList = IntStream.range(0, 10)
                .mapToObj(i -> Thread.ofVirtual().unstarted(() -> {
                    var currThread = Thread.currentThread(); // Capture the current thread before acquiring the lock
                    if (i == 0) {
                        System.out.println(currThread); //  1
                    }
                    synchronized (lock) { //  2
                        try {
                            Thread.sleep(25); //  3
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    if (i == 0) {
                        System.out.println(Thread.currentThread()); //  4
                        // If the thread is pinned, the thread before and after acquiring the lock should be the same
                        if (currThread == Thread.currentThread()) {
                            System.out.println(i + " Same OS thread before and after acquiring the lock. PINNING OCCURRED");
                        } else {
                            System.out.println(i + " Different OS threads, pinning did not occur");
                        }
                    }
                })).toList();
        threadList.forEach(Thread::start);
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }


    private static void f_benchmark(String type, ExecutorService executor, Runnable task, int numTasks) {
        Instant start = Instant.now(); // 4
        AtomicLong completedTasks = new AtomicLong();
        try (executor) { // 5
            IntStream.range(0, numTasks)
                    .forEach(i -> executor.submit(() -> {
                        task.run();
                        completedTasks.incrementAndGet(); // 6
                    }));
        } // 7
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        double throughput = (double) completedTasks.get() / duration * 1000; // Tasks per second // 8
        System.out.printf("%-25s - Time: %5dms, Throughput: %8.2f tasks/s%n",
                type, duration, throughput);
    }
}
