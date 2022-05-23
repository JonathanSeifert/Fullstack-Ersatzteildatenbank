package com.JS.Ersatzteildatenbank.models;

import lombok.*;
import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "county")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "country_id")
    private String countryId;
    @Column(name = "country_name")
    private String countryName;
}
