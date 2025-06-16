package com.sagar.MovieBookingSystem.Util;

import com.sagar.MovieBookingSystem.DTO.BookingDTO;
import com.sagar.MovieBookingSystem.Entity.Booking;
import com.sagar.MovieBookingSystem.Entity.BookingStatus;
import com.sagar.MovieBookingSystem.Entity.Show;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class BookingUtil {

    public BookingDTO convertEntityToDTO(Booking booking){
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setUserId(booking.getUser().getId());
        bookingDTO.setShowId(booking.getShow().getId());
        bookingDTO.setNumberOfSeats(booking.getNumberOfSeats());
        bookingDTO.setSeatNumbers(booking.getSeatNumbers());
        bookingDTO.setPrice(booking.getPrice());
        bookingDTO.setBookingStatus(booking.getBookingStatus());
        bookingDTO.setBookingTime(booking.getBookingTime());
        return bookingDTO;
    }

    public boolean isSeatAvailable(Show show, Integer numberOfSeats){
        Integer bookedSeats = show.getBooking().stream()
                .filter(booking -> booking.getBookingStatus() != BookingStatus.CANCELLED)
                .mapToInt(Booking::getNumberOfSeats)
                .sum();

        return show.getTheater().getTheaterCapacity() - bookedSeats >= numberOfSeats;
    }

    public void validateDuplicateSeats(Show show, List<String> seatNumbers){
        Set<String> occupiedSeats = show.getBooking().stream()
                .filter(booking->booking.getBookingStatus() != BookingStatus.CANCELLED)
                .flatMap(booking->booking.getSeatNumbers().stream())
                .collect(Collectors.toSet());

        List<String> duplicateSeats = seatNumbers.stream()
                .filter(occupiedSeats::contains)
                .toList();

        if(!duplicateSeats.isEmpty()){
            throw new RuntimeException("Seats are already booked");
        }

    }

    public Double calculateTotalAmount(Double price,Integer numberOfSeats){
        return price * numberOfSeats;
    }

    public void validateCancellation(Booking booking){
        if(booking.getBookingStatus() != BookingStatus.CANCELLED){
            throw new RuntimeException("Booking is already cancelled.");
        }

        LocalDateTime showTime = booking.getShow().getShowTime();
        LocalDateTime deadLineTime = showTime.minusHours(2);

        if(LocalDateTime.now().isAfter(deadLineTime)){
            throw new RuntimeException("Cannot cancel the booking.");
        }



    }
}
