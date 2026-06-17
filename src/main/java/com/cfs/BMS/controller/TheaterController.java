package com.cfs.BMS.controller;

import com.cfs.BMS.dto.TheaterRequest;
import com.cfs.BMS.entity.Theater;
import com.cfs.BMS.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @PostMapping("/addTheater")
    public ResponseEntity<Theater> add(@RequestBody TheaterRequest request)
    {
        return ResponseEntity.ok(theaterService.addTheater(request));
    }

    @GetMapping
    public ResponseEntity<List<Theater>> getAllTheater()
    {
        return ResponseEntity.ok(theaterService.getAllTheater());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Theater> getTheaterById(@PathVariable Long id)
    {
        return ResponseEntity.ok(theaterService.getTheaterById(id));
    }

    @GetMapping("/city/{id}")
    public ResponseEntity<List<Theater>> getTheaterByCity(@PathVariable Long id)
    {
        return ResponseEntity.ok(theaterService.getTheaterByCity(id));
    }

}
