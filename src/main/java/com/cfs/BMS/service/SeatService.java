package com.cfs.BMS.service;

import com.cfs.BMS.dto.SeatRequest;
import com.cfs.BMS.entity.Screen;
import com.cfs.BMS.entity.Seat;
import com.cfs.BMS.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreenService screenService;

    public Seat addSeat(SeatRequest seat)
    {
        Screen screen = screenService.getScreenById(seat.getScreenId());
        Seat seat1 = Seat.builder()
                .seatNumber(seat.getSeatNumber())
                .seat_type(seat.getSeatType())
                .col(seat.getCol())
                .row(seat.getRow())
                .screen(screen)
                .build();

        return seatRepository.save(seat1);
    }

    public List<Seat> getSeatByScreen(Long screenId)
    {
        return seatRepository.findByScreenId(screenId);
    }

    public Seat getSeatById(Long id)
    {
        return seatRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Seat not found with id: "+id));
    }

}
