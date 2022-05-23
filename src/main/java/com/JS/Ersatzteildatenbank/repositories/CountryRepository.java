package com.JS.Ersatzteildatenbank.repositories;

import com.JS.Ersatzteildatenbank.models.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country,Integer> {
    Country findByCountryId(String countryId);
}
