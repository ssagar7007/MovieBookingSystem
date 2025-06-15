package com.sagar.MovieBookingSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Movie {
    @Id
    @GeneratedValue()
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String duration;
    private LocalDate releaseDate;
    private String language;
    @OneToMany(mappedBy = "movie",fetch = FetchType.LAZY)
    private List<Show> show;
}
