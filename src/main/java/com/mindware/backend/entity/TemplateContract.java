package com.mindware.backend.entity;

import lombok.Data;

@Data
public class TemplateContract {

    private String id;

    private String fileName;

    private String pathContract;

    private String detail;

    private String active;

    private String typeSavingBox;

    private Integer totalParticipants;

    private String isYunger;

    private String typeAccount;

    private String category;
}
