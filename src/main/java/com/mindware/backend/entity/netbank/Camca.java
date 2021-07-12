package com.mindware.backend.entity.netbank;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Camca {

    private Integer camcacage;

    private String camcancta;

    private Integer camcacmon;

    private Double camcasact;

    private LocalDate camcafapt;

    private Integer camcastus;
}
