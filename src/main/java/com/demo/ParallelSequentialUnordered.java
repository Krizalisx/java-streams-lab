package com.demo;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ParallelSequentialUnordered {

    public static void main(String[] args) {

        Runnable r = () -> {};
        r.run();

        Consumer<Integer> c = elem -> {};
        c.accept(4);

        Supplier<String> s = () -> "Who is it?";
        String sup = s.get();


        int[] counter = new int[]{0}; // [0]
        AtomicInteger atomicCounter = new AtomicInteger(0);

        StringBuilder stringBuilder = new StringBuilder();
        StringBuffer stringBuffer = new StringBuffer();

        var count = IntStream.iterate(0, i -> ++i)
//            .filter(val -> true)
            .parallel()
//            .limit(100) // joining ordered
            .unordered()
//            .sorted()
            .peek(i -> {
                ++counter[0];
                atomicCounter.incrementAndGet();
            })
            .mapToObj(String::valueOf)
            .peek(str -> {
                stringBuilder.append(str + ", ");
                stringBuffer.append(str + ", ");
            })
            .limit(100) // joining unordered
            .collect(Collectors.joining(", "));

        System.out.println("Stream output: " + count);

        System.out.println("Counter: " + Arrays.toString(counter));
        System.out.println("atomicCounter: " + atomicCounter);

        System.out.println("stringBuilder: " + stringBuilder);
        System.out.println("stringBuffer: " + stringBuffer);


    }
}
