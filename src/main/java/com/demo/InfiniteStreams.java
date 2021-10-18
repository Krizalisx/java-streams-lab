package com.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.SneakyThrows;

public class InfiniteStreams {

    @SneakyThrows
    public static void main(String[] args) {
        Path path = Paths.get("src/main/resources/words_alpha.txt");
        Set<String> words = new HashSet<>(Files.readAllLines(path));

        Map<Integer, Long> wordsLengthByCount = words.stream().collect(Collectors.groupingBy(
            word -> word.length(),
            Collectors.counting()
        ));
        System.out.println(wordsLengthByCount);

        String letters = "abcdefghijklmnopqrstuvwxyz";
        char[] lettersArray = letters.toCharArray(); // [a, b, c ...]

        List<String> lettersList = IntStream.range(0, lettersArray.length)
            .mapToObj(i -> String.valueOf(lettersArray[i]))
            .collect(Collectors.toList());

        System.out.println(lettersList);

        final int minLetters = 4;
        final int maxLetters = 10;

        Random generalRandom = new Random(43); // side effects!
        Stream.generate(() -> generalRandom)
            .map(random -> random.nextInt(maxLetters))
//            .parallel()
            .filter(i -> i >= minLetters)
            .map(wordLength -> Stream.generate(() -> generalRandom)
                    .map(random -> random.nextInt(lettersList.size()))
                    .limit(wordLength)
                    .map(i -> lettersList.get(i))
//                .reduce("", (lhs, rhs) -> lhs + rhs)
                    .collect(Collectors.joining())
            )
//            .sorted()
            .distinct()
            .filter(word -> words.contains(word))
            .limit(10)
//            .peek(System.out::println)
            .forEachOrdered(System.out::println);

    }
}
