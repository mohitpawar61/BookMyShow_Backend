package com.cfs.BMS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shows")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id",nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "screen_id",nullable = false)
    private Screen screen;

    @Column(unique = false)
    private LocalDate showDate;

    @Column(unique = false)
    private LocalTime start_time;

    private LocalTime endTime;

    private Double ticketPrice;




}
