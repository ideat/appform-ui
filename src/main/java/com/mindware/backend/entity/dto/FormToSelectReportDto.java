package com.mindware.backend.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormToSelectReportDto {
    private String id;

    private Integer idClient;

    private String idAccount;

    private String cardNumber;

    private String nameTypeForm;

    private String categoryTypeForm;
}
