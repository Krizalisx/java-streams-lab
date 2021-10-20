package com.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
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

        Random generalRandom = new Random(); // side effects!
        Stream.generate(() -> generalRandom)
            .map(random -> random.nextInt(maxLetters))
//            .parallel()
//            .sequential()
            .filter(i -> i >= minLetters)
            .map(wordLength -> Stream.generate(() -> generalRandom)
                    .map(random -> random.nextInt(lettersList.size()))
                    .limit(wordLength)
                    .map(i -> lettersList.get(i))
//                .reduce("", (lhs, rhs) -> lhs + rhs)
                    .collect(Collectors.joining())
            )
//            .sorted()
            .sequential()
//            .parallel() // Investigate problems with forEachOrdered
            .unordered()
//            .distinct()
            .filter(word -> words.contains(word))
            .filter(distinctByKey(word -> word.length()))
            .limit(6)
//            .peek(System.out::println)
            .forEach(System.out::println);

        IntStream.iterate(0, i -> ++i)
            .limit(10)
//            .sorted()
            .parallel()
            .unordered()
            .map(x -> x * 2)
            .forEach(System.out::println);

    }


    public static Predicate<String> distinctByKey(Function<String, Integer> keyMapper) {
        HashSet<Integer> integers = new HashSet<>();
        return elem -> integers.add(keyMapper.apply(elem));
    }

//        Predicate<String> result = new Predicate<String>() {
////            HashSet<Integer> integers = new HashSet<>();
//            Set<Integer> integers = ConcurrentHashMap.newKeySet();
//
//            @Override
//            public boolean test(String word) {
//                Integer length = keyMapper.apply(word);
//
//                return integers.add(length);
//            };
//        };
//        return result;

}
