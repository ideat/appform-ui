package com.mindware.backend.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormToSelectReportDto {
    private String id;

    private Integer idClient;

    private String idAccount;

    private String account;

    private String cardNumber;

    private String nameTypeForm;

    private String categoryTypeForm;

    public String getTypFormAccount(){

        return this.nameTypeForm +
                (this.categoryTypeForm.equals("VARIOS")?"":("*"+this.categoryTypeForm))+
                "*" + this.idAccount;
    }
}
