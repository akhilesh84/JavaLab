package com.demo.utils;

import java.time.Duration;
import java.util.function.*;

/*
 * @author: Akhilesh Yadav
 * @created: 09/11/2025
 * @description: Retry policy contract for handling retry logic in operations.
 * */
@FunctionalInterface
public interface RetryPolicy {
    // Define signature of a generic method that takes an input of some type TInput or its derieved class, an exception, and returns a Duration
    // indicating how long to wait before retrying.
    <TInput> Duration getDelay(TInput input, Exception exception, int retryAttempt);

    default void execute(Runnable operation) {
        int attempt = 0;
        Duration delay = Duration.ZERO;

        while(true) {
            try {
                Thread.sleep(delay);
                operation.run();
                return;
            }
            catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            catch (Exception e) {
                delay = getDelay(null, e, attempt++);
                if (delay == null) throw e;
            }
        }
    }

    default <T> T eval(Supplier<? extends T> supplier) {
        int attempt = 0;
        Duration delay = Duration.ZERO;

        while(true) {
            try {
                Thread.sleep(delay);
                return supplier.get();
            }
            catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            catch (Exception e) {
                delay = getDelay(null, e, attempt++);
                if (delay == null) throw e;
            }
        }
    }

    /*
    * Logically the below functions are simply wrappers around the above two methods to provide syntactic sugar
    * for executing Consumers, BiConsumers, Functions and BiFunctions with retry logic.
    * We can use the core functions above to invoke a supplier or consumer with any number or arguments by using lambdas.
    * This avoids code duplication and keeps the implementation DRY.
    * */
    default <T> void execute(Consumer<? super T> consumer, T arg) { execute(() -> consumer.accept(arg)); }
    default <T, U> void execute(BiConsumer<? super T, ? super U> consumer, T arg1, U arg2) { execute(() -> consumer.accept(arg1, arg2)); }

    default <T, U> U eval(Function<? super T, ? extends U> function, T arg) { return eval(() -> function.apply(arg)); }
    default <T1, T2, U> U eval(BiFunction<? super T1, ? super T2, ? extends U> function, T1 arg1, T2 arg2) { return eval(() -> function.apply(arg1, arg2)); }
}
