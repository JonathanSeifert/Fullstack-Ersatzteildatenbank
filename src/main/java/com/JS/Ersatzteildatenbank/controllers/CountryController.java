package com.JS.Ersatzteildatenbank.controllers;

import com.JS.Ersatzteildatenbank.models.Country;
import com.JS.Ersatzteildatenbank.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class CountryController {
    @Autowired
    CountryRepository countryRepository;

    @GetMapping(value="/countries")
    public ResponseEntity<List<Country>> getCountries() {
        try {
            List<Country> countries = new ArrayList<>();
            countryRepository.findAll().forEach(countries::add));
            if (countries.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(countries, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
