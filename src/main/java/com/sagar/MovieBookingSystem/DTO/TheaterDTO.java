package com.sagar.MovieBookingSystem.DTO;

import lombok.Data;

@Data
public class TheaterDTO {
    private Long id;
    private String name;
    private String theaterLocation;
    private Integer theaterCapacity;
    private String theaterScreenType;
}
