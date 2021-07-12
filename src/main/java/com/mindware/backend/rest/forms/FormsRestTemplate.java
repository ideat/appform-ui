package com.mindware.backend.rest.forms;

import com.mindware.backend.entity.Forms;
import com.mindware.backend.entity.netbank.dto.DataFormDto;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FormsRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public Forms create(Forms forms){
        final String uri = url + "/form/create";
        HttpEntity<Forms> entity = new HttpEntity<>(forms,HeaderJwt.getHeader());
        ResponseEntity<Forms> response = restTemplate.postForEntity(uri,entity,Forms.class);
        return response.getBody();
    }

    public DataFormDto findDataFormDtoFormSavingBoxByCageAndAccount(Integer cage, String account, String category){
        final String uri = url + "/form/findDataFormDtoFormSavingBoxOrDpfByCageAndAccount";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("cage",cage.toString());
        headers.add("account",account);
        headers.add("category_type_form",category);
        HttpEntity<DataFormDto> entity = new HttpEntity<>(headers);

        ResponseEntity<DataFormDto> response =  restTemplate.exchange(uri, HttpMethod.GET, entity, DataFormDto.class);

        return response.getBody();
    }

    public Forms findByIdAccountAndTypeFormAndCategoryTypeForm(String idAccount, String nameTypeForm, String categoryTypeForm ){
        final String uri = url + "/form/findByIdAccountAndTypeFormAndCategoryTypeForm";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id_account",idAccount);
        headers.add("name_type_form",nameTypeForm);
        headers.add("category_type_form",categoryTypeForm);
        HttpEntity<Forms> entity = new HttpEntity<>(headers);

        ResponseEntity<Forms> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Forms.class);

        return response.getBody();

    }
}
