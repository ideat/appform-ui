package com.mindware.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Signatory {

    private String id;

    private String fullName;

    private String idCard;

    private String extension;

    private String position;

    private String powerAttorney;

    private Date datePowerAttorney;

    private Integer numberNotary;

    private String notaryName;

    private Integer idOffice;
}
