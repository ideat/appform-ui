package com.mindware.backend.entity.dto;

import lombok.Data;

@Data
public class SignUpDto {

    private String fullName;

    private String login;

    private char[] password;
}
