package com.cfs.BMS.entity;

import com.cfs.BMS.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "seat_row")
    private String row;

    @Column(name = "seat_col")
    private Integer col;

    @Enumerated(EnumType.STRING)
    private SeatType seat_type;

    @ManyToOne
    @JoinColumn(name = "screen_id",nullable = false)
    private Screen screen;

}
