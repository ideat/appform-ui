package com.mindware.backend.rest.netbank;

import com.mindware.backend.entity.netbank.Gbcon;
import com.mindware.backend.entity.netbank.dto.GbageDto;
import com.mindware.backend.entity.netbank.dto.GbageLabDto;
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
public class GbconRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<Gbcon> findAll(){

        final String uri = url+"/gbcon/findAll";

        HttpHeaders headers = HeaderJwt.getHeader();
        HttpEntity<Gbcon> entity = new HttpEntity<>(headers);

        ResponseEntity<Gbcon[]> result = restTemplate.exchange(uri, HttpMethod.GET,entity,Gbcon[].class);
        Gbcon[] gbconList = result.getBody();

        return Arrays.asList(gbconList);
    }
}
