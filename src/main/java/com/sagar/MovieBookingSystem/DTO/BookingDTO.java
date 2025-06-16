package com.sagar.MovieBookingSystem.DTO;

import com.sagar.MovieBookingSystem.Entity.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingDTO {
    private Long id;
    private Integer numberOfSeats;
    private LocalDateTime bookingTime;
    private Double price;
    private BookingStatus bookingStatus;
    private List<String> seatNumbers;
    private Long userId;
    private Long showId;
}
