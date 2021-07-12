package com.mindware.backend.entity.dto;

import lombok.Data;

@Data
public class UserDto {

    private String id;

    private String fullName;

    private String login;

    private String menu;

    private String rolName;

    private String token;
}
