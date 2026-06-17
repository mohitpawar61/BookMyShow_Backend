package com.cfs.BMS.controller;

import com.cfs.BMS.dto.BookingReq;
import com.cfs.BMS.entity.Booking;
import com.cfs.BMS.entity.Seat;
import com.cfs.BMS.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public ResponseEntity<Booking> createBooking( @RequestBody BookingReq req)
    {
        return ResponseEntity.ok(bookingService.createBooking(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id)
    {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingByUserId(@PathVariable Long userId)
    {
        return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id)
    {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/show/{showId}/available-seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long showId)
    {
        return ResponseEntity.ok(bookingService.getAvailableSeats(showId));
    }


}
