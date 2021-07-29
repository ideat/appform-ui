package com.mindware.backend.rest.email;

import com.mindware.backend.entity.email.Mail;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MailRestTemplate {

    @Value("${url}")
    private String url;

    private static RestTemplate restTemplate = new RestTemplate();

    public Mail add(Mail mail){
        final String uri = url +"/mail/send";
        HttpEntity<Mail> entity = new HttpEntity<>(mail, HeaderJwt.getHeader());
        ResponseEntity<Mail> response = restTemplate.postForEntity(uri,entity,Mail.class);
        return response.getBody();
    }
//
//    public List<Mail> getByNumberRequest(Integer numberRequest){
//        final String uri = "http://localhost:8080/rest/mail/v1/getByNumberRequest/{number_request}";
//        Map<String,Integer> params = new HashMap<>();
//        params.put("number_request",numberRequest);
//        HttpEntity<Mail[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
//        ResponseEntity<Mail[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Mail[].class,params);
//        return Arrays.asList(response.getBody());
//    }
//
//    public Mail getById(UUID id){
//        final String uri = "http://localhost:8080/rest/mail/v1/getById/{id}";
//        Map<String,UUID> params = new HashMap<>();
//        params.put("id",id);
//        HttpEntity<Mail> entity = new HttpEntity<>(HeaderJwt.getHeader());
//        ResponseEntity<Mail> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Mail.class,params);
//        return response.getBody();
//    }
//
//    public void update(Mail mail){
//        final String uri = "http://localhost:8080/rest/mail/v1/update";
//        HttpEntity<Mail> entity = new HttpEntity<>(mail,HeaderJwt.getHeader());
//        restTemplate.put(uri,entity);
//    }
}
