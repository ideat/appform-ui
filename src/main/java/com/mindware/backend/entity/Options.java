package com.mindware.backend.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class Options {

    private UUID id;

    private String name;

    private boolean assigned;

    private boolean write;

    private boolean read;

    private String description;


}
