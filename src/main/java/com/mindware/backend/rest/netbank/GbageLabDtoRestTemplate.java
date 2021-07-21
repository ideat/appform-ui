package com.mindware.backend.rest.netbank;

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
public class GbageLabDtoRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<GbageLabDto> findGbageLabDtoByIdCard(String searchBy){

        final String uri = url+"/gbageLabDto/findGblabDtoByIdCard";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("search",searchBy);
        HttpEntity<GbageDto> entity = new HttpEntity<>(headers);

        ResponseEntity<GbageLabDto[]> result = restTemplate.exchange(uri, HttpMethod.GET,entity,GbageLabDto[].class);
        GbageLabDto[] gbageLabDtos = result.getBody();

        return Arrays.asList(gbageLabDtos);
    }

    public List<GbageLabDto> findGbageLabDtoByCage(String searchBy){

        final String uri = url+"/gbageLabDto/findGblabDtoByCage";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("search",searchBy);
        HttpEntity<GbageDto> entity = new HttpEntity<>(headers);

        ResponseEntity<GbageLabDto[]> result = restTemplate.exchange(uri, HttpMethod.GET,entity,GbageLabDto[].class);
        GbageLabDto[] gbageLabDtos = result.getBody();

        return Arrays.asList(gbageLabDtos);
    }

}
