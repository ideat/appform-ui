package com.mindware.backend.entity;

import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;

@Data
public class Users {

    private String id;

    private String login;

    private String fullName;

    private String password;

    private String rolName;

    private String image;

    private Date dateUpdatePassword;

    private String email;

    private Integer numDaysValidity;

    private String state;

    private Date createDate;

    public String getInitials(){
        if(fullName!=null) {
            String[] arr1 = fullName.split(" ");
            String[] arr = Arrays.stream(arr1)
                    .map(String::trim)
                    .filter(Predicate.isEqual("").negate())
                    .toArray(String[]::new);
            if(arr.length == 1){
                return (arr[0].substring(0,2)).toUpperCase();
            }else {
                return (arr[0].substring(0,1) + arr[1].substring(0,1)).toUpperCase();
            }
        }
        return "N/A";
    }

}
