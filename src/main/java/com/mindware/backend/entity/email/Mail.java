package com.mindware.backend.entity.email;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Mail {
    private UUID id;

    private Integer numberRequest;

    private String loginUser;

    private LocalDateTime sendDate;

    private String mailFrom;

    private String mailTo;

    private String mailCc;

    private String mailSubject;

    private String mailContent;

    private String contentType;

    private String attachments;

    public Mail() {
        contentType = "text/plain";
    }
}
