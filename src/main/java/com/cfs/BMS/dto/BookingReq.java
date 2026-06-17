package com.cfs.BMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BookingReq {

    private Long userId;
    private Long showId;

    private List<Long> seatId;
}
