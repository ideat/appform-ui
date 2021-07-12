package com.mindware.backend.entity;

import lombok.Data;

@Data
public class Rol {

    private String id;

    private String name;

    private String description;

    private String options;

    private String scopes;
}
