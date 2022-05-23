package com.JS.Ersatzteildatenbank.models;

import lombok.*;
import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "city_name", nullable = false)
    private String cityName;
    @Column(name = "zip_code")
    private String zipCode;
    @ManyToOne(targetEntity = County.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "county_id", referencedColumnName = "county_id")
    private String county_id;
}
