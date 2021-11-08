package com.demo;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class CompletableFutureExamples {

    @Test
    public void creatingCompletableFuture() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> System.out.println("runnable"));
        CompletableFuture.supplyAsync(() -> "supplier");

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> hello());
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> world(), Executors.newSingleThreadExecutor());
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("!");

        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {});

        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> {System.out.println("Hello Kitty");},
            Executors.newFixedThreadPool(2));

    }

    @Test
    public void getCompletableFuture() {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> hello());
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> world(), Executors.newSingleThreadExecutor());
//        CompletableFuture<String> future3 = CompletableFuture.completedFuture("!");
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "!";
        });


//        try {
//            System.out.println(future1.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            System.out.println(future2.get(1, TimeUnit.SECONDS));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//
        System.out.println(future3.getNow("."));

        System.out.println(future3.join());

        try {
            System.out.println(future3.join());
        } catch (CompletionException e) {
            e.printStackTrace();
        }

        assertEquals(RuntimeException.class, CompletionException.class.getSuperclass());

    }

    @Test
    public void thenApply() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                return hello();
            })
            .thenApply(s -> {
                System.out.println(Thread.currentThread().getName());
                return s + " ";
            })
            .thenApplyAsync(s -> {
                System.out.println(Thread.currentThread().getName());
                return s + world();
            }, Executors.newCachedThreadPool())
            .thenApply(s -> {
                System.out.println(Thread.currentThread().getName());
                return s + "!";
            });

        //...

        System.out.println(future.get());

    }

    @Test
    public void thenApplyAsync() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                return hello();
            })
            .thenApplyAsync(s -> {
                System.out.println(Thread.currentThread().getName());
                return s + " ";
            })
            .thenApplyAsync(s -> {
                System.out.println(Thread.currentThread().getName());
                return s + world();
            })
            .thenApplyAsync(s -> s + "!");

        System.out.println(future.get());


        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> hello())
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " "))
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + world()))
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + "!"));

        System.out.println(future1.get());
    }

    @Test
    public void thenApplyAcceptRun() throws ExecutionException, InterruptedException {

        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> hello())
                .thenApply(s -> s + " World")
                .thenAccept(s1 -> System.out.print(s1))
                .thenRun(() -> System.out.println("!"));

        System.out.println(future);
        System.out.println(future.get());

    }

    @Test
    public void allOfAnyOf() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> hello());
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> world(), Executors.newSingleThreadExecutor());
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("!");

        CompletableFuture<Object> futureAny123 = CompletableFuture.anyOf(future1, future2, future3);
        CompletableFuture<Void> futureAll123 = CompletableFuture.allOf(future1, future2, future3);

        futureAll123.thenRun(() -> System.out.println("ALL"));
        futureAny123.thenRun(() -> System.out.println("ANY"));

        System.out.println(futureAll123.get());
        System.out.println(futureAny123.get());
    }

    @Test
    public void either() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(this::hello);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(this::world);

        CompletableFuture<Void> eitherFuture = future1.acceptEither(future2, val -> System.out.println(val));
        System.out.println(eitherFuture.get());
    }

    @Test
    public void thenCombine() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(this::hello);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(this::world);

        CompletableFuture<String> futureCombined = future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2 + "!");
        System.out.println(futureCombined.get());
    }

    @Test
    public void acceptBoth() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(this::hello);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(this::world);

        CompletableFuture<Void> futureBoth = future1.thenAcceptBoth(future2, (s1, s2) -> System.out.println(s1 + " " + s2 + "!"));
        System.out.println(futureBoth.get());
    }

    @Test
    public void exceptionally() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> unsafeHello())
                .exceptionally(throwable -> throwable.getMessage());

        System.out.println(future1.get());
    }

    @Test
    public void handle() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> unsafeHello())
                .handle((value, throwable) -> value == null ? "Exception: " + throwable.getMessage() : value);

        System.out.println(future1.get());
    }

    @Test
    public void stream() {

        long start = System.currentTimeMillis();
        int sum = IntStream.of(1000, 2000, 3000)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName());
                    sleep(i);
                    return i;
                }))
//                .sorted((o1, o2) -> 1)
                .mapToInt(CompletableFuture::join)
                .sum();
        long end = System.currentTimeMillis();

        System.out.println("Execution time: " + (end - start));
        System.out.println(sum);
    }

    private String world() {
        sleep(1000);
        return "World";
    }

    private String hello() {
        sleep(500);
        return "Hello";
    }

    private String unsafeHello()
    {
        sleep(500);
        if (ThreadLocalRandom.current().nextBoolean()) throw new RuntimeException();
        return "Hello";
    }

    private void sleep(int timeout) {
        try {
            MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
