package com.JS.Ersatzteildatenbank.models;

import lombok.*;
import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "county", uniqueConstraints = {@UniqueConstraint(name = "county_idName_unique",
                                                                columnNames = {"county_id", "county_name"}),
                                            @UniqueConstraint(name = "county_id_country_id_unique",
                                                                columnNames = {"county_id", "country_id"})})
public class County {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "county_id")
    private String countyId;
    @Column(name = "county_name")
    private String countyName;
    @ManyToOne(targetEntity = Country.class, cascade = CascadeType.ALL)
    @JoinColumn(name="country_id", referencedColumnName = "country_id")
    private Country country;
}
