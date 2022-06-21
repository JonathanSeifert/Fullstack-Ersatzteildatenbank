package com.JS.Ersatzteildatenbank.model;

import javax.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Land {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    //ISO-3166-2
    @Column
    String iso_3166_2;
    @Column
    String name;

    public Land(String iso, String name){
        this.iso_3166_2 = iso;
        this.name = name;
    }
}
