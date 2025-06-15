package com.sagar.MovieBookingSystem.DTO;

import lombok.Data;

import java.time.LocalDate;
@Data
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String duration;
    private LocalDate releaseDate;
    private String language;
}
