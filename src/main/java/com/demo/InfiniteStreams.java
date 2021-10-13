package com.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.SneakyThrows;

public class InfiniteStreams {

    @SneakyThrows
    public static void main(String[] args) {
        Path path = Paths.get("src/main/resources/words_alpha.txt");
        HashSet<String> words = new HashSet<>(Files.readAllLines(path));

        String letters = "abcdefghijklmnopqrstuvwxyz";
        char[] lettersArray = letters.toCharArray(); // [a, b, c ...]

        var lettersList = letters.chars()
            .mapToObj(c -> (char) c)
            .map(character -> String.valueOf(character))
            .collect(Collectors.toList());

        List<String> collect = IntStream.range(0, lettersArray.length)
            .mapToObj(i -> String.valueOf(lettersArray[i]))
            .collect(Collectors.toList());

        System.out.println(lettersList);

        System.out.println(collect);



    }
}
