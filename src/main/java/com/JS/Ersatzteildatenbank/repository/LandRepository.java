package com.JS.Ersatzteildatenbank.repository;

import com.JS.Ersatzteildatenbank.model.Land;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandRepository extends JpaRepository<Land, Integer> {
    public Land findByIso_3166_2(String iso);
}
