package com.mindware.backend.rest.netbank;

import com.mindware.backend.entity.netbank.Cacon;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.List;

@Service
public class CaconRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<Cacon> getByPref(Integer caconpref){

        final String uri = url + "/cacon/findCaconByPref";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("caconpref",caconpref.toString());

        HttpEntity<Cacon> entity = new HttpEntity<>(headers);
        ResponseEntity<Cacon[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Cacon[].class);

        return Arrays.asList(response.getBody());
    }
}
