package com.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

@AllArgsConstructor
public class Demo {

    private static HttpClient client = HttpClient.newHttpClient();
    private static Gson gson = new GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create();

    @SneakyThrows
    public static void main(String[] args) {

        String response = executeGetRequest("https://countriesnow.space/api/v0.1/countries/population/cities");

        Datas datas = gson.fromJson(response, Datas.class);

        datas.getCities().stream()
//            .map(City::getCountry)
            .distinct()
            .filter(city -> city.getCountry().startsWith("R") || city.getCountry().startsWith("r"))
//            .sorted(Comparator.comparingInt(String::length))
            .flatMap(city -> city.populationCounts.stream())
            .forEach(System.out::println);


        List.of(List.of(1, 2, 3), List.of(4, 5, 6), List.of(7, 8)) // 1, 2, 3, 4, 5, 6, 7, 8
            .stream()
//            .flatMap(integers -> integers.stream())
            .map(integers -> integers.stream())
            .forEach(System.out::println);

        Boolean isMercuryInProperPhase = getMercury();

        String plans = isMercuryInProperPhase ? "Lucky day" : "Stay at home";

        if (isMercuryInProperPhase) { // Possible NPE!
            //...
        }
    }


    private static Boolean getMercury() {
        return new Random().nextBoolean();
    }

    @SneakyThrows
    private static String executeGetRequest(String uri) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(uri))
            .GET()
            .build();

        return client.send(request, BodyHandlers.ofString()).body();
    }

    @Data
    static class Datas {
        @SerializedName("data")
        private List<City> cities;
    }

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    static class City {
        private String city;
        @EqualsAndHashCode.Include
        private String country;
        private List<Population> populationCounts;

    }

    @Data
    static class Population {
        private String year;
        private String value;
        private String sex;
        private String reliabilty;
    }

}