package com.mindware.backend.entity.netbank.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
public class GbageDto {

    private Integer gbagecage;

    private String gbagenomb;

    private String gbagendid;

    private Date gbagefnac;

    private Date gbagefreg;

    private Date openingDate;

    private String accountName;

    private String accountCode;

    private String currency;

    private Integer secundaryCage; //Codigo del tutor en caso de menores o codigo cliente manejo conjunto

    private String typeAccount;

    public LocalDate getGbagefregConvert(){
        return gbagefreg.toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDate();
    }
    public LocalDate getOpeningDateConvert(){
        if (openingDate!=null) {
            return openingDate.toInstant()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate();
        } return null;
    }
}
