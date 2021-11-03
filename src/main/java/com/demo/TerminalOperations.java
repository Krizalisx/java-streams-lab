package com.demo;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class TerminalOperations {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public static void main(String[] args) {

        var collect1 = getStudents().stream()
            .parallel()
            .unordered()
//            .findAny()
            .findFirst();

        var collect2 = getStudents().stream()
            .parallel()
            .unordered()
            .anyMatch(student -> student.getAge() >= 18);

        var collect3 = getStudents().stream()
            .collect(Collectors.partitioningBy(student -> student.getSubject().stream()
                .anyMatch(subject -> "Math".equals(subject.getSubjectName()) && "A".equals(subject.getMark())),
//                Collectors.counting()
//                Collectors.partitioningBy(student -> student.getAge() > 18)
                Collectors.mapping(student -> student.getFirstName() + " " + student.getLastName(), Collectors.toList())
            ));

        AtomicInteger counter = new AtomicInteger(0); // BAD BAD NOT GOOD!
        var collect4 = getStudents().stream()
            .peek(elem -> counter.incrementAndGet())
            .collect(Collectors.groupingBy(student -> student.getAge(),
                    Collectors.collectingAndThen(Collectors.counting(), aLong -> Double.valueOf(aLong) / counter.get())
//                Collectors.mapping(student -> student.getFirstName() + " " + student.getLastName(), Collectors.toList())
            ));

        var collect5 = getStudents().stream()
            .collect(Collectors.toMap(
                student -> student.getAge(),
//                student -> student.getFirstName() + " " + student.getLastName(),
                student -> List.of(student.getFirstName() + " " + student.getLastName()),
                (lhs, rhs) -> {
                    ArrayList<String> res = new ArrayList<>(lhs);
                    res.addAll(rhs);
                    return res;
                },
//                () -> new TreeMap<>(Comparator.comparing(Function.identity(), (lhs, rhs) -> rhs - lhs)
                () -> new TreeMap<>(Comparator.<Integer>reverseOrder()
            )));

        var collect6 = getStudents().stream()
            .collect(Collectors.toMap(
                Function.identity(),
                student -> student.getSubject()
            ))
            .entrySet()
            .stream()
            .flatMap(studentListEntry -> studentListEntry.getValue().stream().map(
                subject -> Map.entry(studentListEntry.getKey(), subject)
            ))
            .collect(Collectors.groupingBy(
                studentSubjectEntry -> studentSubjectEntry.getValue().getSubjectName(),
                Collectors.groupingBy(studentSubjectEntry -> studentSubjectEntry.getValue().getMark(), Collectors.counting()
            )));

        var collect7 = getStudents().stream()
            .collect(Collector.of(
                () -> new HashMap<String, Long>(),
                (map, student) -> {
                    String marks = student.getSubject().stream().map(subject -> subject.getMark()).sorted().collect(Collectors.joining());
//                    Long studentsCount = map.get(marks);
//                    if (isNull(studentsCount)) {
//                        studentsCount = 1L;
//                    } else {
//                        ++studentsCount;
//                    }
//                    map.put(marks, studentsCount);
                    map.merge(marks, 1L, ((aLong, aLong2) -> aLong + aLong2));
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                }
            ));

        var theSameAsCustom = getStudents().stream()
            .collect(Collectors.groupingBy(student -> student.getSubject().stream().map(subject -> subject.getMark()).sorted().collect(Collectors.joining()),
//                Collectors.collectingAndThen(Collectors.toList(), students -> students.size())
                Collectors.counting()
                ));

        System.out.println(gson.toJson(collect7));
        System.out.println(gson.toJson(theSameAsCustom));
        // {"AAA": 2, "ABF": 4, "FFA": 1, "FFF": 3}


    }

    public static List<Student> getStudents() {
        return List.of(
            new Student("Liam", "Smith", 22, List.of(subject("Math", "A"), subject("History", "A"), subject("Music", "A"))),
            new Student("Oliver", "Johnson", 20, List.of(subject("Math", "B"), subject("History", "D"), subject("Music", "A"))),
            new Student("William", "Williams", 18, List.of(subject("Math", "A"), subject("History", "F"), subject("Music", "A"))),
            new Student("James", "Brown", 28, List.of(subject("Math", "F"), subject("History", "C"), subject("Music", "B"))),
            new Student("Lucas", "Jones", 22, List.of(subject("Math", "A"), subject("History", "F"), subject("Music", "B"))),
            new Student("Mason", "Garcia", 18, List.of(subject("Math", "C"), subject("History", "F"), subject("Music", "C"))),
            new Student("Ethan", "Miller", 29, List.of(subject("Math", "F"), subject("History", "A"), subject("Music", "F"))),
            new Student("Bob", "Dilan", 29, List.of(subject("Math", "F"), subject("History", "F"), subject("Music", "A")))
        );
    }

    public static Subject subject(String name, String mark) {
        return new Subject(name, mark);
    }


}

@Data
@AllArgsConstructor
class Student {

    private String firstName;
    private String lastName;
    private Integer age;
    private List<Subject> subject;
}

@Data
@AllArgsConstructor
class Subject {

    private String subjectName;
    private String mark;
}