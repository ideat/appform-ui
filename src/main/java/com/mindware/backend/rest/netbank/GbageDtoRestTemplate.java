package com.mindware.backend.rest.netbank;

import com.mindware.backend.entity.netbank.dto.GbageDto;
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
public class GbageDtoRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<GbageDto> findGbageDto(String searchBy, String criteria){
        final String uri = url+"/search";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("criteria",criteria);
        headers.add("search",searchBy);
        HttpEntity<GbageDto> entity = new HttpEntity<>(headers);

        ResponseEntity<GbageDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,GbageDto[].class);

        return Arrays.asList(response.getBody());

    }
}
