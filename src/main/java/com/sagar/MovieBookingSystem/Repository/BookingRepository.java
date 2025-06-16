package com.sagar.MovieBookingSystem.Repository;

import com.sagar.MovieBookingSystem.Entity.Booking;
import com.sagar.MovieBookingSystem.Entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByShowId(Long showId);
    List<Booking> findByBookingStatus(BookingStatus bookingStatus);

}
