package com.cfs.BMS.dto;

import com.cfs.BMS.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SeatRequest {

    private String seatNumber;
    private String row;
    private Integer col;
    private SeatType seatType;
    private Long screenId;



}
