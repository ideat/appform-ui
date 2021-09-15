package com.mindware.backend.rest.contract;

import com.mindware.backend.util.HeaderJwt;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContractRestTemplate {
    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public byte[] contract(Integer codeClient, String account, String typeForm,
                           String categoryTypeForm, String isTutor, String typeAccount){
        final String uri = url + "/contract/getFileContract";

        typeForm = typeForm; //TODO: Borrar la linea

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("account",account);
        headers.add("code-client",codeClient.toString());
        headers.add("category-type-form",categoryTypeForm);
        headers.add("type-form",typeForm);
        headers.add("is-tutor",isTutor);
        headers.add("login", VaadinSession.getCurrent().getAttribute("login").toString());
        headers.add("plaza", VaadinSession.getCurrent().getAttribute("plaza").toString());
        headers.add("type-account", typeAccount);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);

        return response.getBody();
    }
}
