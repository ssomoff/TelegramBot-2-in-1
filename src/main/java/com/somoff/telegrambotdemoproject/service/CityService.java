package com.somoff.telegrambotdemoproject.service;

import com.somoff.telegrambotdemoproject.client.CityClient;
import com.somoff.telegrambotdemoproject.entity.City;
import com.somoff.telegrambotdemoproject.entity.User;
import com.somoff.telegrambotdemoproject.exception.ServiceException;
import com.somoff.telegrambotdemoproject.repo.CityRepository;
import com.somoff.telegrambotdemoproject.repo.UserRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final UserRepositoty userRepositoty;
    private final CityClient cityClient;

    public void downloadCityInfo() throws ServiceException {
        try {
            if (!cityRepository.existsById(1L))
                cityRepository.saveAll(cityClient.getListCities());
        } catch (RuntimeException e) {
            throw new ServiceException("Ошибка загрузки городов", e);

        }
    }

    public void registerUser(Long chatId) {
        if (!userRepositoty.existsById(chatId)) {
            User user = new User();
            user.setId(chatId);
            user.setFirstSymbol('?');
            user.setCities(new ArrayList<>());
            System.out.println(user);
            userRepositoty.save(user);
        }
    }

    public void clearPlayCities(Long chatId) {
        if (userRepositoty.existsById(chatId)) {
            User user = userRepositoty.findById(chatId).get();
            List<City> cities = user.getCities();
            cities.clear();
            user.setFirstSymbol('?');
            user.setCities(cities);
            userRepositoty.save(user);
        }
    }

    public String getFirstSymbol(Long chatId){
        User user = userRepositoty.findById(chatId).get();
        if(user.getFirstSymbol()!='?')
            return "Назовите город на букву " + user.getFirstSymbol();
        return "Назовите город";
    }

    public String findCityInBD(Long chatId, String message) {

        User user = userRepositoty.findById(chatId).get();
        char el = message.charAt(0);

        if (el != user.getFirstSymbol() && user.getFirstSymbol() != '?')
            return "Неверный город. Попробуй еще";

        City findCity = cityRepository.findCityByName(message).stream().findFirst().orElse(null);

        if (findCity == null)
            return "Нет такого города, введите заново";

        List<City> cities = user.getCities();

        if (cities.contains(findCity))
            return "Такой город уже был, введите заново";

        cities.add(findCity);

        String str = findCity.getCity();

        int i = str.length() - 1;
        char endSymbol = str.charAt(i);
        while (endSymbol == 'ь' || endSymbol == 'ъ' || endSymbol == 'ы' || endSymbol == 'й')
            endSymbol = str.charAt(i--);
        List<City> cityList = cityRepository.findCitiesByCityLike(String.valueOf(endSymbol).toUpperCase())
                .stream().toList();

        City city;
        do {
            Random r = new Random();
            city = cityList.get(r.nextInt(cityList.size()));
        } while (cities.contains(city));

        cities.add(city);
        user.setCities(cities);

        i = city.getCity().length() - 1;
        endSymbol = city.getCity().toUpperCase().charAt(i);
        while (endSymbol == 'Ь' || endSymbol == 'Ъ' || endSymbol == 'Ы' || endSymbol == 'Й')
            endSymbol = city.getCity().toUpperCase().charAt(i--);
        user.setFirstSymbol(endSymbol);

        userRepositoty.save(user);
        var text = "Мой город %s . Твой ход";
        return String.format(text, city.getCity());
    }
}

