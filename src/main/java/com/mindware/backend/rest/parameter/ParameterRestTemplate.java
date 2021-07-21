package com.mindware.backend.rest.parameter;

import com.mindware.backend.entity.Parameter;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class ParameterRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<Parameter> findAll() {
        final String uri = url+"/parameter/findAll";
        HttpEntity<Parameter[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Parameter[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Parameter[].class);

        return Arrays.asList(response.getBody());
    }

    public Parameter create(Parameter parameter){
        final String uri = url+"/parameter/create";
        HttpEntity<Parameter> entity = new HttpEntity<>(parameter, HeaderJwt.getHeader());
        ResponseEntity<Parameter> response = restTemplate.postForEntity(uri,entity,Parameter.class);
        return response.getBody();
    }
}
