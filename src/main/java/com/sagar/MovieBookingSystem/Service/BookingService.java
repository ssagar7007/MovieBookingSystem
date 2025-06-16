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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingUtil bookingUtil;

    public BookingDTO createBooking(BookingDTO bookingDTO){
        Show show = showRepository.findById(bookingDTO.getShowId()).orElseThrow(
                ()->new RuntimeException("No show found with id "+bookingDTO.getShowId()));

        if(!bookingUtil.isSeatAvailable(show,bookingDTO.getNumberOfSeats())){
            throw new RuntimeException("Seats not available");
        }

        if(bookingDTO.getSeatNumbers().size() != bookingDTO.getNumberOfSeats()){
            throw new RuntimeException("Seat numbers and Number Of Seats should be equal");
        }

        bookingUtil.validateDuplicateSeats(show,bookingDTO.getSeatNumbers());

        User user = userRepository.findById(bookingDTO.getUserId()).orElseThrow(
                ()->new RuntimeException("User does not exists."));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setNumberOfSeats(bookingDTO.getNumberOfSeats());
        booking.setSeatNumbers(bookingDTO.getSeatNumbers());
        booking.setPrice(bookingUtil.calculateTotalAmount(show.getPrice(),bookingDTO.getNumberOfSeats()));
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        booking = bookingRepository.save(booking);
        return bookingUtil.convertEntityToDTO(booking);
    }


    public List<BookingDTO> getUserBookings(Long userId){
        List<Booking> bookingList = bookingRepository.findByUserId(userId);
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        bookingList.forEach(booking -> {
            bookingDTOS.add(bookingUtil.convertEntityToDTO(booking));
        });

        return bookingDTOS;
    }

    public BookingDTO confirmBooking(Long id){
        Booking booking = bookingRepository.findById(id).orElseThrow(()->new RuntimeException("Booking id does not exists."));
        if(booking.getBookingStatus() != BookingStatus.PENDING){
            throw new RuntimeException("Booking is not in pending state.");
        }

        //Payment process
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        return bookingUtil.convertEntityToDTO(bookingRepository.save(booking));
    }

    public BookingDTO cancelBooking(Long id){
        Booking booking = bookingRepository.findById(id).orElseThrow(()->new RuntimeException("Booking id does not exists."));

        bookingUtil.validateCancellation(booking);
        booking.setBookingStatus(BookingStatus.CANCELLED);
        return bookingUtil.convertEntityToDTO(bookingRepository.save(booking));
    }

    public List<BookingDTO> getShowBooking(Long showId){
        List<Booking> bookingList = bookingRepository.findByShowId(showId);
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        bookingList.forEach(booking -> {
            bookingDTOS.add(bookingUtil.convertEntityToDTO(booking));
        });

        return bookingDTOS;
    }

    public List<BookingDTO> getBookingByStatus(BookingStatus bookingStatus){
        List<Booking> bookingList = bookingRepository.findByBookingStatus(bookingStatus);
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        bookingList.forEach(booking -> {
            bookingDTOS.add(bookingUtil.convertEntityToDTO(booking));
        });

        return bookingDTOS;
    }
}
