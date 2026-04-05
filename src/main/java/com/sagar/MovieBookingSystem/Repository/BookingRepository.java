package com.sagar.MovieBookingSystem.Repository;

import com.sagar.MovieBookingSystem.Entity.Booking;
import com.sagar.MovieBookingSystem.Entity.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByShowId(Long showId);
    
    List<Booking> findByBookingStatus(BookingStatus bookingStatus);
    
    /**
     * Finds all bookings for a show with pessimistic write lock to prevent concurrent modifications.
     * Useful for concurrent seat reservation handling.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.show.id = :showId")
    List<Booking> findByShowIdWithLock(@Param("showId") Long showId);
    
    /**
     * Finds a booking by ID with pessimistic write lock.
     */
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findById(@Param("id") Long id);
}
