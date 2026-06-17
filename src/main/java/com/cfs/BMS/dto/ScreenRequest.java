package com.cfs.BMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ScreenRequest {

    private String name;
    private Integer totalSeats;
    private Long theaterId;
}
