package com.sagar.MovieBookingSystem.Controller;

import com.sagar.MovieBookingSystem.DTO.BookingDTO;
import com.sagar.MovieBookingSystem.Entity.BookingStatus;
import com.sagar.MovieBookingSystem.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/createBooking")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO){
        return ResponseEntity.ok(bookingService.createBooking(bookingDTO));
    }

    @GetMapping("/getUserBooking/{id}")
    public ResponseEntity<List<BookingDTO>> getUserBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.getUserBookings(id));
    }

    @GetMapping("/getShowBooking/{id}")
    public ResponseEntity<List<BookingDTO>> getShowBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.getShowBooking(id));
    }
    @PutMapping("{id}/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.confirmBooking(id));
    }
    @PutMapping("{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/getBookingByStatus/{bookingStatus}")
    public ResponseEntity<List<BookingDTO>> getBookingByStatus(@PathVariable BookingStatus bookingStatus){
        return ResponseEntity.ok(bookingService.getBookingByStatus(bookingStatus));
    }



}
