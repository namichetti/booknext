package com.amichettinestor.booknext.booknext.dto;

import lombok.Data;

@Data
public class PersonDto {

    private String name;

    private String lastName;

    private LocationRequestDto location;
}
