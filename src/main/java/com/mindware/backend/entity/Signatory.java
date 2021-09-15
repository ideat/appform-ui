package com.mindware.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Signatory {

    private String id;

    private String fullName;

    private String idCard;

    private String position;

    private String powerNotary;

    private Date datePowerNotary;

    private Integer numberNotary;

    private String notaryName;

    private Integer plaza;

    private String tradeRegistration;

    private String active;
}
