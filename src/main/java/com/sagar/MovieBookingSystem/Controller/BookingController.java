package com.sagar.MovieBookingSystem.Controller;

import com.sagar.MovieBookingSystem.DTO.BookingDTO;
import com.sagar.MovieBookingSystem.Entity.BookingStatus;
import com.sagar.MovieBookingSystem.Service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@Slf4j
public class BookingController {
    
    @Autowired
    private BookingService bookingService;

    /**
     * Creates a new booking with transactional consistency and Redis-based seat locking.
     * Requires user authentication.
     *
     * @param bookingDTO The booking details including show ID, seat numbers, and user ID
     * @return ResponseEntity with the created booking
     */
    @PostMapping("/createBooking")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        log.info("API: Creating booking for show {}", bookingDTO.getShowId());
        try {
            BookingDTO created = bookingService.createBooking(bookingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Failed to create booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves all bookings for a specific user.
     * User can only retrieve their own bookings, admin can retrieve any user's bookings.
     *
     * @param id The user ID
     * @return ResponseEntity with list of bookings
     */
    @GetMapping("/getUserBooking/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BookingDTO>> getUserBooking(@PathVariable Long id) {
        log.info("API: Fetching bookings for user {}", id);
        return ResponseEntity.ok(bookingService.getUserBookings(id));
    }

    /**
     * Retrieves all bookings for a specific show.
     * Accessible to authenticated users.
     *
     * @param id The show ID
     * @return ResponseEntity with list of bookings
     */
    @GetMapping("/getShowBooking/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BookingDTO>> getShowBooking(@PathVariable Long id) {
        log.info("API: Fetching bookings for show {}", id);
        return ResponseEntity.ok(bookingService.getShowBooking(id));
    }

    /**
     * Confirms a pending booking and releases the seat locks.
     * Booking confirmation triggers seat lock release from Redis.
     *
     * @param id The booking ID to confirm
     * @return ResponseEntity with the confirmed booking
     */
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingDTO> confirmBooking(@PathVariable Long id) {
        log.info("API: Confirming booking {}", id);
        try {
            BookingDTO confirmed = bookingService.confirmBooking(id);
            return ResponseEntity.ok(confirmed);
        } catch (RuntimeException e) {
            log.error("Failed to confirm booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Cancels a booking within the cancellation window and releases seat locks.
     *
     * @param id The booking ID to cancel
     * @return ResponseEntity with the cancelled booking
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id) {
        log.info("API: Cancelling booking {}", id);
        try {
            BookingDTO cancelled = bookingService.cancelBooking(id);
            return ResponseEntity.ok(cancelled);
        } catch (RuntimeException e) {
            log.error("Failed to cancel booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves all bookings with a specific status.
     * Admin endpoint for monitoring and reporting.
     *
     * @param bookingStatus The status to filter by
     * @return ResponseEntity with list of bookings
     */
    @GetMapping("/getBookingByStatus/{bookingStatus}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getBookingByStatus(@PathVariable BookingStatus bookingStatus) {
        log.info("API: Fetching bookings with status {}", bookingStatus);
        return ResponseEntity.ok(bookingService.getBookingByStatus(bookingStatus));
    }

}
