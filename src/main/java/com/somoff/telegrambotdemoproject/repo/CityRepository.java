package com.somoff.telegrambotdemoproject.repo;

import com.somoff.telegrambotdemoproject.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    @Query("select e from City e where e.city = ?1")
    List<City> findCityByName(String name);

    @Query("select e from City e where e.city like ?1%")
    List<City> findCitiesByCityLike(String name);

}
