package com.mindware.backend.rest.signatory;

import com.mindware.backend.entity.Signatory;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class SignatoryRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<Signatory> findAll(){
        final String uri = url+"/signatory/findAll";
        HttpEntity<Signatory[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Signatory[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity,Signatory[].class);

        return Arrays.asList(response.getBody());
    }

    public Signatory getByPlaza(String plaza){
        final String uri = url+"/signatory/getByPlaza";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("plaza", plaza);
        HttpEntity<Signatory> entity = new HttpEntity<>(headers);
        ResponseEntity<Signatory> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Signatory.class);

        return response.getBody();
    }

    public Signatory create(Signatory signatory){
        final String uri = url+"/signatory/add";

        HttpEntity<Signatory> entity = new HttpEntity<>(signatory, HeaderJwt.getHeader());
        ResponseEntity<Signatory> response = restTemplate.postForEntity(uri,entity,Signatory.class);

        return response.getBody();
    }


}
