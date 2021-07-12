package com.mindware.backend.entity;

import lombok.Data;

@Data
public class TypeForm {
    private String id;

    private String category;

    private String name;

    private  String services;

    private String operations;

    private Integer state;
}
