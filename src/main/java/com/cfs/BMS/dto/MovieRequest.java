package com.cfs.BMS.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequest {

    @Column(nullable = false)
    private String title;

    private String description;

    private String genre;

    private String language;

    private Integer durationMinutes;

    private Double rating;

    private LocalDate releaseDate;

    private String posterUrl;
}
