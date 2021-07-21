package com.mindware.backend.rest.netbank;

import com.mindware.backend.entity.netbank.Gbpmt;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GbpmtRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public Gbpmt findAll(){
        final String uri = url+"/gbpmt/findAll";

        HttpHeaders headers = HeaderJwt.getHeader();
        HttpEntity<Gbpmt> entity = new HttpEntity<>(headers);

        ResponseEntity<Gbpmt> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Gbpmt.class);

        return response.getBody();
    }
}
