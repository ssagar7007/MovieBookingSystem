package com.sagar.MovieBookingSystem.Service;

import com.sagar.MovieBookingSystem.DTO.BookingDTO;
import com.sagar.MovieBookingSystem.Entity.Booking;
import com.sagar.MovieBookingSystem.Entity.BookingStatus;
import com.sagar.MovieBookingSystem.Entity.Show;
import com.sagar.MovieBookingSystem.Entity.User;
import com.sagar.MovieBookingSystem.Repository.BookingRepository;
import com.sagar.MovieBookingSystem.Repository.ShowRepository;
import com.sagar.MovieBookingSystem.Repository.UserRepository;
import com.sagar.MovieBookingSystem.Util.BookingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingUtil bookingUtil;
    
    @Autowired
    private SeatLockService seatLockService;

    /**
     * Creates a new booking with Redis-based distributed seat locking and transactional consistency.
     * Uses SERIALIZABLE isolation level to prevent concurrent booking conflicts.
     *
     * @param bookingDTO The booking details
     * @return The created booking DTO
     * @throws RuntimeException if seats are unavailable or validation fails
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        log.info("Creating booking for show {} with {} seats", bookingDTO.getShowId(), bookingDTO.getNumberOfSeats());
        
        // Step 1: Acquire Redis locks for all requested seats
        if (!seatLockService.acquireMultipleLocks(bookingDTO.getShowId(), bookingDTO.getSeatNumbers())) {
            log.warn("Failed to acquire seat locks for show {}", bookingDTO.getShowId());
            throw new RuntimeException("One or more seats are currently being booked by another user. Please try again.");
        }

        try {
            // Step 2: Fetch show with pessimistic lock to prevent concurrent modifications
            Show show = showRepository.findById(bookingDTO.getShowId())
                    .orElseThrow(() -> new RuntimeException("No show found with id " + bookingDTO.getShowId()));

            // Step 3: Validate seat availability
            if (!bookingUtil.isSeatAvailable(show, bookingDTO.getNumberOfSeats())) {
                log.warn("Insufficient seats available for show {}", bookingDTO.getShowId());
                throw new RuntimeException("Seats not available for this show");
            }

            // Step 4: Validate seat count matches request
            if (bookingDTO.getSeatNumbers().size() != bookingDTO.getNumberOfSeats()) {
                throw new RuntimeException("Seat numbers count and requested number of seats must match");
            }

            // Step 5: Validate no duplicate seats in request and database
            bookingUtil.validateDuplicateSeats(show, bookingDTO.getSeatNumbers());

            // Step 6: Fetch user
            User user = userRepository.findById(bookingDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User does not exist"));

            // Step 7: Create and save booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setShow(show);
            booking.setNumberOfSeats(bookingDTO.getNumberOfSeats());
            booking.setSeatNumbers(bookingDTO.getSeatNumbers());
            booking.setPrice(bookingUtil.calculateTotalAmount(show.getPrice(), bookingDTO.getNumberOfSeats()));
            booking.setBookingStatus(BookingStatus.PENDING);
            booking.setBookingTime(LocalDateTime.now());

            booking = bookingRepository.save(booking);
            log.info("Booking created successfully with id {}", booking.getId());
            
            return bookingUtil.convertEntityToDTO(booking);
            
        } catch (RuntimeException e) {
            // Release locks on failure
            log.error("Booking creation failed: {}", e.getMessage());
            seatLockService.releaseMultipleLocks(bookingDTO.getShowId(), bookingDTO.getSeatNumbers());
            throw e;
        }
    }

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId The user ID
     * @return List of bookings for the user
     */
    @Transactional(readOnly = true)
    public List<BookingDTO> getUserBookings(Long userId) {
        log.debug("Fetching bookings for user {}", userId);
        List<Booking> bookingList = bookingRepository.findByUserId(userId);
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        bookingList.forEach(booking -> {
            bookingDTOS.add(bookingUtil.convertEntityToDTO(booking));
        });
        return bookingDTOS;
    }

    /**
     * Confirms a pending booking and releases the seat locks.
     * Uses SERIALIZABLE isolation to ensure consistency.
     *
     * @param id The booking ID to confirm
     * @return The confirmed booking DTO
     * @throws RuntimeException if booking is not in PENDING state
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDTO confirmBooking(Long id) {
        log.info("Confirming booking with id {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking id does not exist"));
        
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending state and cannot be confirmed");
        }

        try {
            // Payment process would happen here (placeholder)
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            Booking saved = bookingRepository.save(booking);
            
            // Release seat locks after successful confirmation
            seatLockService.releaseMultipleLocks(booking.getShow().getId(), booking.getSeatNumbers());
            log.info("Booking {} confirmed and locks released", id);
            
            return bookingUtil.convertEntityToDTO(saved);
        } catch (Exception e) {
            log.error("Error confirming booking {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to confirm booking", e);
        }
    }

    /**
     * Cancels a booking within the cancellation window.
     * Uses SERIALIZABLE isolation for consistency.
     *
     * @param id The booking ID to cancel
     * @return The cancelled booking DTO
     * @throws RuntimeException if booking cannot be cancelled
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDTO cancelBooking(Long id) {
        log.info("Cancelling booking with id {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking id does not exist"));

        bookingUtil.validateCancellation(booking);
        
        try {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            Booking saved = bookingRepository.save(booking);
            
            // Release seat locks after cancellation
            seatLockService.releaseMultipleLocks(booking.getShow().getId(), booking.getSeatNumbers());
            log.info("Booking {} cancelled and locks released", id);
            
            return bookingUtil.convertEntityToDTO(saved);
        } catch (Exception e) {
            log.error("Error cancelling booking {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to cancel booking", e);
        }
    }

    /**
     * Retrieves all bookings for a specific show.
     *
     * @param showId The show ID
     * @return List of bookings for the show
     */
    @Transactional(readOnly = true)
    public List<BookingDTO> getShowBooking(Long showId) {
        log.debug("Fetching bookings for show {}", showId);
        List<Booking> bookingList = bookingRepository.findByShowId(showId);
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        bookingList.forEach(booking -> {
            bookingDTOS.add(bookingUtil.convertEntityToDTO(booking));
        });
        return bookingDTOS;
    }

    /**
     * Retrieves all bookings with a specific status.
     *
     * @param bookingStatus The booking status to filter by
     * @return List of bookings with the specified status
     */
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingByStatus(BookingStatus bookingStatus) {
        log.debug("Fetching bookings with status {}", bookingStatus);
        List<Booking> bookingList = bookingRepository.findByBookingStatus(bookingStatus);
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        bookingList.forEach(booking -> {
            bookingDTOS.add(bookingUtil.convertEntityToDTO(booking));
        });

        return bookingDTOS;
    }
}
