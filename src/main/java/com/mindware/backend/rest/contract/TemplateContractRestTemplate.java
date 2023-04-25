package com.mindware.backend.rest.contract;

import com.mindware.backend.entity.Forms;
import com.mindware.backend.entity.TemplateContract;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TemplateContractRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public TemplateContract create(TemplateContract templateContract){

        final String uri = url + "/template-contract/create";
        HttpEntity<TemplateContract> entity = new HttpEntity<>(templateContract, HeaderJwt.getHeader());
        ResponseEntity<TemplateContract> response = restTemplate.postForEntity(uri,entity,TemplateContract.class);
        return response.getBody();
    }

    public TemplateContract updateActive(TemplateContract templateContract){
        final String uri = url + "/template-contract/updatestate";
        HttpEntity<TemplateContract> entity = new HttpEntity<>(templateContract,HeaderJwt.getHeader());
        ResponseEntity<TemplateContract> response = restTemplate.postForEntity(uri,entity,TemplateContract.class);
        return response.getBody();

    }

    public List<TemplateContract> findAll(){
        final String uri = url + "/template-contract/findAll";
        HttpEntity<TemplateContract[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateContract[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, TemplateContract[].class);

        return Arrays.asList(response.getBody());
    }

    public List<TemplateContract> findByCategory(String category){
        final String uri = url + "/template-contract/findByCateroy";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("category",category);

        HttpEntity<TemplateContract[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateContract[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, TemplateContract[].class);

        return Arrays.asList(response.getBody());
    }

    public String  upload( String pathFileTemp, String fileName) {
        final String uri = url + "/template-contract/upload";
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        FileSystemResource value = new FileSystemResource(new File(pathFileTemp));
        bodyMap.add("file",value);
        bodyMap.add("filename",fileName);

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(bodyMap,headers);

        ResponseEntity<String> reponse = restTemplate.exchange(uri, HttpMethod.POST,request,String.class);
        return reponse.getBody();
    }

    public TemplateContract getByFileName(String fileName){
        final String uri = url + "/template-contract/getByFileName/{filename}";;
        Map<String,String> params = new HashMap<>();
        params.put("filename",fileName);
        HttpEntity<TemplateContract> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateContract> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateContract.class,params);
        return response.getBody();

    }
}
