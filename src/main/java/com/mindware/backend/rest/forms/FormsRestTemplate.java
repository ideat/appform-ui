package com.mindware.backend.rest.forms;

import com.mindware.backend.entity.Forms;
import com.mindware.backend.entity.dto.FormToSelectReportDto;
import com.mindware.backend.entity.netbank.dto.DataFormDto;
import com.mindware.backend.util.HeaderJwt;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormsRestTemplate {

    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public Forms create(Forms forms){
        final String uri = url + "/form/create";
        forms.setOriginModule("AUTO-FORM");
        HttpEntity<Forms> entity = new HttpEntity<>(forms,HeaderJwt.getHeader());
        ResponseEntity<Forms> response = restTemplate.postForEntity(uri,entity,Forms.class);
        return response.getBody();
    }

    public DataFormDto findDataFormDtoFormSavingBoxByCageAndAccount(Integer cage, String account, String category, String isTutor){
        final String uri = url + "/form/findDataFormDtoFormSavingBoxOrDpfByCageAndAccount";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("cage",cage.toString());
        headers.add("account",account);
        headers.add("category_type_form",category);
        headers.add("is-tutor",isTutor);
        HttpEntity<DataFormDto> entity = new HttpEntity<>(headers);

        ResponseEntity<DataFormDto> response =  restTemplate.exchange(uri, HttpMethod.GET, entity, DataFormDto.class);

        return response.getBody();
    }

    public List<DataFormDto> findDataFormForDigitalBank(Integer cage){
        final String uri = url + "/form/findDataFormForDigitalBank";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("cage",cage.toString());
        HttpEntity<DataFormDto[]> entity = new HttpEntity<>(headers);

        ResponseEntity<DataFormDto[]> response =  restTemplate.exchange(uri, HttpMethod.GET, entity, DataFormDto[].class);

        return Arrays.asList(response.getBody());
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

    public Forms findByIdClientIdAccountAndTypeFormAndCategoryTypeForm(String idClient, String idAccount, String nameTypeForm, String categoryTypeForm ){
        final String uri = url + "/form/findByIdClientIdAccountAndTypeFormAndCategoryTypeForm";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id_client",idClient);
        headers.add("id_account",idAccount);
        headers.add("name_type_form",nameTypeForm);
        headers.add("category_type_form",categoryTypeForm);
        HttpEntity<Forms> entity = new HttpEntity<>(headers);

        ResponseEntity<Forms> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Forms.class);

        return response.getBody();

    }

    public List<Forms> findByUserTypeFormAndCategoryTypeForm(String idUser, String nameTypeForm, String categoryTypeForm ){
        final String uri = url + "/form/findByUserTypeFormAndCategoryTypeForm";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("user",idUser);
        headers.add("name_type_form",nameTypeForm);
        headers.add("category_type_form",categoryTypeForm);
        HttpEntity<Forms> entity = new HttpEntity<>(headers);

        ResponseEntity<Forms[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Forms[].class);

        return Arrays.asList(response.getBody());

    }

    public List<Forms> findByTypeFormAndCategoryTypeForm( String nameTypeForm, String categoryTypeForm ){
        final String uri = url + "/form/findByTypeFormAndCategoryTypeForm";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("name_type_form",nameTypeForm);
        headers.add("category_type_form",categoryTypeForm);
        HttpEntity<Forms> entity = new HttpEntity<>(headers);

        ResponseEntity<Forms[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Forms[].class);

        return Arrays.asList(response.getBody());

    }



    public Forms findByIdClientAndTypeFormAndCategoryTypeForm(Integer idClient, String nameTypeForm, String categoryTypeForm ){
        final String uri = url + "/form/findByIdClientAndTypeFormAndCategoryTypeForm";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id_client",idClient.toString());
        headers.add("name_type_form",nameTypeForm);
        headers.add("category_type_form",categoryTypeForm);
        HttpEntity<Forms> entity = new HttpEntity<>(headers);

        ResponseEntity<Forms> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Forms.class);

        return response.getBody();

    }

    public List<FormToSelectReportDto> findFormSelectReportByIdclient(Integer idclient){
        final String uri = url+"/formtoselectreport/findByIdClient/{idclient}";
        Map<String,Integer> params = new HashMap<>();
        params.put("idclient",idclient);

        HttpEntity<FormToSelectReportDto> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<FormToSelectReportDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,FormToSelectReportDto[].class,params);
        return Arrays.asList(response.getBody());
    }

    public byte[] report(Integer codeClient, String idAccount, String typeForm, String categoryTypeForm, String isTutor){
        final String uri = url + "/form/getFormSavingBankAndDpfReport";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id_account",idAccount);
        headers.add("code_client",codeClient.toString());
        headers.add("category_type_form",categoryTypeForm);
        headers.add("type_form",typeForm);
        headers.add("is-tutor",isTutor);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);

        return response.getBody();
    }

    public byte[] reportDigitalBank(Integer codeClient, String idAccountServiceOperation, String typeForm, String categoryTypeForm){
        final String uri = url + "/form/getFormDigitalBankDtoReport";
        String officeName = VaadinSession.getCurrent().getAttribute("name-office").toString();
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id_account_service_operation",idAccountServiceOperation);
        headers.add("code_client",codeClient.toString());
        headers.add("category_type_form",categoryTypeForm);
        headers.add("type_form",typeForm);
        headers.add("name-office",officeName);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);

        return response.getBody();
    }

    public byte[] reportDebitCard(Integer codeClient, String idAccountServiceOperation, String typeForm, String categoryTypeForm){
        final String uri = url + "/form/getFormDebitCardDtoReport";
        String officeName = VaadinSession.getCurrent().getAttribute("name-office").toString();

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id_account_service_operation",idAccountServiceOperation);
        headers.add("code_client",codeClient.toString());
        headers.add("category_type_form",categoryTypeForm);
        headers.add("type_form",typeForm);
        headers.add("name-office",officeName);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);

        return response.getBody();
    }

    public byte[] reportDeliverDebitCard(Integer codeClient, String idAccountServiceOperation, String typeForm, String categoryTypeForm){
        final String uri = url + "/form/getFormDeliverDebitCardReport";
        String officeName = VaadinSession.getCurrent().getAttribute("name-office").toString();

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id_account_service_operation",idAccountServiceOperation);
        headers.add("code_client",codeClient.toString());
        headers.add("category_type_form",categoryTypeForm);
        headers.add("type_form",typeForm);
        headers.add("name-office",officeName);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);

        return response.getBody();
    }

    public byte[] reportVerificationIdtCard(String id, String login){
        final String uri = url + "/form/getFormVerifyIdCardDtoReport";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("id",id);
        headers.add("login",login);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);

        return response.getBody();
    }

    public byte[] reportSelectedReports(Integer codClient, String login, String listReports, String officeName){
        final String uri = url + "/form/getSelectedReport";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("code_client", codClient.toString());
        headers.add("login",login);
        headers.add("office_name", officeName);
        headers.add("list_reports",listReports);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);

        return response.getBody();

    }

}
