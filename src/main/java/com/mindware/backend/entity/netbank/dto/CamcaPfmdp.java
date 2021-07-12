package com.mindware.backend.entity.netbank.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CamcaPfmdp {

    private String gbagenomb;

    private String gbagendid;

    private String account;

    private Date createDate;

    private String typeAccount;

    private String state;
}
