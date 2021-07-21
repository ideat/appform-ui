package com.mindware.backend.rest.netbank;

import com.mindware.backend.entity.netbank.dto.AdusrOfi;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AdusrOfiRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public AdusrOfi findByLogin(String login){
        final String uri = url+"/adusrofi/findByLogin";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("login",login);
        HttpEntity<AdusrOfi> entity = new HttpEntity<>(headers);

        ResponseEntity<AdusrOfi> response = restTemplate.exchange(uri, HttpMethod.GET,entity,AdusrOfi.class);

        return response.getBody();


    }
}
