package com.sagar.MovieBookingSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_show_id", columnList = "show_id"),
    @Index(name = "idx_booking_user_id", columnList = "user_id"),
    @Index(name = "idx_booking_status", columnList = "booking_status"),
    @Index(name = "idx_booking_show_user", columnList = "show_id, user_id")
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer numberOfSeats;
    private LocalDateTime bookingTime;
    private Double price;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booking_seat_numbers", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat_number")
    private List<String> seatNumbers;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;
}
