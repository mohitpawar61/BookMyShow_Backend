package com.cfs.BMS.service;

import com.cfs.BMS.dto.ScreenRequest;
import com.cfs.BMS.entity.Screen;
import com.cfs.BMS.entity.Theater;
import com.cfs.BMS.repository.ScreenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheaterService theaterService;


    public Screen addScreen(ScreenRequest request) {

        Theater theater = theaterService.getTheaterById(request.getTheaterId());

        Screen screen = Screen.builder()
                .totalSeats(request.getTotalSeats())
                .name(request.getName())
                .theater(theater)
                .build();

        return screenRepository.save(screen);
    }

    public List<Screen> getAllScreen()
    {
        return screenRepository.findAll();
    }

    public Screen getScreenById(Long id)
    {
        return screenRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Screen not found with id: "+id));
    }

    public List<Screen> getScreenByTheater(Long theaterId)
    {
        return screenRepository.findByTheaterId(theaterId);
    }


}
