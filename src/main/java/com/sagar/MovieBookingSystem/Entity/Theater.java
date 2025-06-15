package com.sagar.MovieBookingSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String theaterLocation;
    private Integer theaterCapacity;
    private String theaterScreenType;
    @OneToMany(mappedBy = "theater",fetch = FetchType.LAZY)
    private List<Show> show;

}
