package com.JS.Ersatzteildatenbank.repositories;

import com.JS.Ersatzteildatenbank.models.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City,Integer> {
    City findByZipCode(String zipCode);
}