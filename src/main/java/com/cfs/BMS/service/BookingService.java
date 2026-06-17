package com.cfs.BMS.service;

import com.cfs.BMS.dto.BookingReq;
import com.cfs.BMS.entity.Booking;
import com.cfs.BMS.entity.Seat;
import com.cfs.BMS.entity.Show;
import com.cfs.BMS.entity.User;
import com.cfs.BMS.enums.BookingStatus;
import com.cfs.BMS.repository.BookingRepository;
import com.cfs.BMS.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserService userService;
    private final ShowService showService;

    @Transactional
    public Booking createBooking(BookingReq request)
    {
        User user = userService.getUserById(request.getUserId());
        Show show = showService.getShowById(request.getShowId());

        if(request.getSeatId() == null || request.getSeatId().isEmpty())
        {
            throw new RuntimeException("At least one seat must be selected");
        }

        List<Long> alreadyBookedSeats= bookingRepository.findBookedSeatIdByShowId(show.getId());
        for(Long seatId:request.getSeatId())
        {
            if(alreadyBookedSeats.contains(seatId))
            {
                throw new RuntimeException("Seat with id "+ seatId +" is already Booked");
            }
        }

        List<Seat> seats = seatRepository.findAllById(request.getSeatId());
        if (seats.size()!=request.getSeatId().size())
        {
            throw new RuntimeException("Some Seats Are Invalid");
        }

        double totalPrice=seats.size()*show.getTicketPrice();
        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .seats(seats)
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();

        return bookingRepository.save(booking);
    }


    public Booking getBookingById(Long id)
    {
        return bookingRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Booking not found with id: "+id));
    }

    public List<Booking> getBookingByUserId(Long userId)
    {
        return bookingRepository.findByUserId(userId);

    }

    public Booking cancelBooking(Long bookingId)
    {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public List<Seat> getAvailableSeats(Long showId)
    {
        Show show = showService.getShowById(showId);
        List<Seat> allSeat = seatRepository.findByScreenId(show.getScreen().getId());
        List<Long> bookingSeatIds = bookingRepository.findBookedSeatIdByShowId(showId);
        return allSeat.stream()
                .filter(seat -> !bookingSeatIds.contains(seat.getId()))
                .toList();
    }



}
