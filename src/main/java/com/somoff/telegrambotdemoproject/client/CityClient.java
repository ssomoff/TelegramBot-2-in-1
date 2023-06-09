package com.somoff.telegrambotdemoproject.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somoff.telegrambotdemoproject.entity.City;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CityClient {

    private final String pathFile = "src/main/resources/static/russia";


    public List<City> getListCities() {

        ObjectMapper objectMapper = new ObjectMapper();
        List<City> cities = new ArrayList<>();
        try {
            City[] cities1 = objectMapper.readValue(new File(pathFile), City[].class);
            cities = Arrays.stream(cities1).toList();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }
}


