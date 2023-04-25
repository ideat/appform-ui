package com.mindware.backend.rest.netbank;

import com.mindware.backend.entity.netbank.Catca;
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
public class CatcaRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<Catca> findAll(){
        final String uri = url + "/catca/findAll";

        HttpHeaders headers = HeaderJwt.getHeader();
        HttpEntity<Catca> entity = new HttpEntity<>(headers);

        ResponseEntity<Catca[]> result = restTemplate.exchange(uri, HttpMethod.GET,entity, Catca[].class);
        Catca[] catcaList = result.getBody();

        return Arrays.asList(catcaList);
    }
}
