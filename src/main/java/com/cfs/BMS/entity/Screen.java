package com.cfs.BMS.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "screens")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer totalSeats;

    @ManyToOne
    @JoinColumn(name = "theater_id",nullable = false)
    private Theater theater;

}
