package com.mindware.backend.rest.contract;

import com.mindware.backend.entity.VariableContract;
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
public class VariableContractRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public VariableContract create(VariableContract variableContract){

        final String uri = url + "/variable-contract/create";
        HttpEntity<VariableContract> entity = new HttpEntity<>(variableContract, HeaderJwt.getHeader());
        ResponseEntity<VariableContract> response = restTemplate.postForEntity(uri,entity,VariableContract.class);
        return response.getBody();
    }

   public List<VariableContract> findAll() {
       final String uri = url + "/variable-contract/findAll";
       HttpEntity<VariableContract[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
       ResponseEntity<VariableContract[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,VariableContract[].class);

       return Arrays.asList(response.getBody());
   }
}
