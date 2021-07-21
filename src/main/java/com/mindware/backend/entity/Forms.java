package com.mindware.backend.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Forms {

    private String id;

    private Integer idClient;

    private String nameTypeForm;

    private String categoryTypeForm;

    private String product;

    private String idAccount;

    private String reference;

    private String description;

    private Integer state;

    private String linkingAccount;

    private String isFinalBeneficiary;

    private String beneficiary; //json

    private String services; //json

    private String operations;//json

    private String accounts; //json

    private String debitAccount;

    private String reasonsDetail;

    private String cardNumber;

    private Double maxAmount;

    private Double maxExtensionAmount;

    private String currency;

    private String idCardForVerification; //json

    private Date creationDate;

    private String creationTime;

    private String createdBy;

    private String idUser;

    private String nameClientVinculation;

    private String documentClientVinculation;

    private String accountServiceOperation; //json

    private String userDigitalBank;
}


