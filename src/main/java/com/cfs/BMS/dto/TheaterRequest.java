package com.cfs.BMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TheaterRequest {

    private String name;
    private String address;
    private Long cityId;

}
