package com.mindware.backend.entity;

import lombok.Data;


@Data
public class AccountServiceOperation {
    private String id;

    private String createDate;

    private String deliverDate;

    private String hourCreate;

    private String account;

    private String services; //json parameter

    private String reasonOpening;

    private Double extensionAmount;

    private Double decreaseAmount;

    private String accountSavingBank;

    private String originModule;

}
