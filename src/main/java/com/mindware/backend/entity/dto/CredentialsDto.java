package com.mindware.backend.entity.dto;

import lombok.Data;

@Data
public class CredentialsDto {

    private String login;

    private char[] password;
}
