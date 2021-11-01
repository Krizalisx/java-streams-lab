package com.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

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



        System.out.println(gson.toJson(collect4));
        System.out.println(collect5.getClass());


    }

    public static List<Student> getStudents() {
        return List.of(
            new Student("Liam", "Smith", 22, List.of(subject("Math", "A"), subject("History", "A"), subject("Music", "A"))),
            new Student("Oliver", "Johnson", 20, List.of(subject("Math", "B"), subject("History", "D"), subject("Music", "A"))),
            new Student("William", "Williams", 18, List.of(subject("Math", "A"), subject("History", "F"), subject("Music", "A"))),
            new Student("James", "Brown", 28, List.of(subject("Math", "F"), subject("History", "C"), subject("Music", "B"))),
            new Student("Lucas", "Jones", 22, List.of(subject("Math", "A"), subject("History", "F"), subject("Music", "B"))),
            new Student("Mason", "Garcia", 18, List.of(subject("Math", "C"), subject("History", "F"), subject("Music", "C"))),
            new Student("Ethan", "Miller", 29, List.of(subject("Math", "F"), subject("History", "F"), subject("Music", "A")))
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