package com.demo;

import static java.lang.Integer.parseInt;

import com.google.common.base.Supplier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.floats.FloatHeapSemiIndirectPriorityQueue;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.components.Figure;

@AllArgsConstructor
public class Demo {

    private static HttpClient client = HttpClient.newHttpClient();
    private static Gson gson = new GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create();

    @SneakyThrows
    public static void main(String[] args) {

//        String response = executeGetRequest("https://countriesnow.space/api/v0.1/countries/population/cities");
        String populationByCountries = executeGetRequest("https://countriesnow.space/api/v0.1/countries/population");

        PopulationByCountriesResponse response = gson.fromJson(populationByCountries, PopulationByCountriesResponse.class);

        String path = "src/main/resources/population.csv";
        writeCsv(response, path);
        printTable(path);
    }

    @SneakyThrows
    private static void printTable(String path) {
        Table table = Table.read().csv(path);
        Figure figure = LinePlot.create("Population", table, "year", "value", "country");
        Plot.show(figure);
    }

    @SneakyThrows
    private static void writeCsv(PopulationByCountriesResponse response, String path) {
        String csvTable = response.getData().stream()
            .dropWhile(country -> !country.getCountry().equalsIgnoreCase("Afghanistan"))
            .filter(country -> {
                    Map<Integer, Integer> populationYearValue = country.getPopulationCounts().stream()
                        .collect(Collectors.toMap(
                            population -> parseInt(population.getYear()),
                            population -> parseInt(population.getValue())
                        ));

                    int maxYear = populationYearValue.keySet().stream()
                        .mapToInt(val -> val)
                        .max()
                        .getAsInt();

                    return populationYearValue.get(maxYear) - populationYearValue.get(maxYear - 5) < 0;
                }
//                .anyMatch(population -> Integer.parseInt(population.getYear()) == 2018 && Integer.parseInt(population.getValue()) < 6_000_000)
            )
            .map(country -> country.getPopulationCounts().stream()
                .map(population -> createRow(country, population))
                .reduce("", (row1, row2) -> String.join("\n", row1, row2))
            )
            .reduce((csv1, csv2) -> csv1 + csv2)
            .orElseThrow(RuntimeException::new);

        System.out.println(csvTable);

        csvTable = "year,value,country,code" + csvTable;

        try (PrintWriter writer = new PrintWriter(path)) {
            writer.write(csvTable);
        }
    }

    private static String createRow(Country country, Population population) {
        return String.join(",", population.getYear(), population.getValue(), '"' + country.getCountry() + '"', country.getCode());
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
    static class PopulationByCountriesResponse {
        private List<Country> data;
    }

    @Data
    static class Country {
        private String country;
        private String code;
        private List<Population> populationCounts;
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