package com.mindware.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Users {

    private String id;

    private String login;

    private String fullName;

    private String password;

    private String rolName;

    private String image;

    private Date dateUpdatePassword;

    private String email;

    private Integer numDaysValidity;

    private String state;

}
