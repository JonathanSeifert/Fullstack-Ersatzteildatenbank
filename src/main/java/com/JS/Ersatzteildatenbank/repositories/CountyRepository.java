package com.JS.Ersatzteildatenbank.repositories;

import com.JS.Ersatzteildatenbank.models.County;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountyRepository extends JpaRepository<County,Integer> {
    County findByCountyId(String countryId);
}
