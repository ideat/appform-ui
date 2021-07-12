package com.mindware.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Beneficiary {
//    @JsonProperty("id")

    private String id;

//    @JsonProperty("fullName")
    private  String fullName;

//    @JsonProperty("telephone")
    private String telephone;

//    @JsonProperty("idCard")
    private String idCard;

//    @JsonProperty("extension")
    private String extension;

//    @JsonProperty("economicActivity")
    private String economicActivity;

//    @JsonProperty("nationality")
    private String nationality;

//    @JsonProperty("address")
    private String address;

//    @JsonProperty("sourceFounds")
    private String sourceFounds;

}
