package com.mindware.backend.rest.user;

import com.mindware.backend.entity.Users;
import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRestTemplate {
    @Value("${url}")
    private String url;

    RestTemplate restTemplate = new RestTemplate();

    public List<Users> findAll(){
        final String uri = url+"/user/findAll";
        HttpEntity<Users[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users[].class);
        return Arrays.asList(response.getBody());
    }

    public Users add(Users users){
        final String uri = url+"/user/add";
        HttpEntity<Users> entity = new HttpEntity<>(users,HeaderJwt.getHeader());
        ResponseEntity<Users> response = restTemplate.postForEntity(uri,entity,Users.class);
        return response.getBody();
    }

    public void updatePassword(Users users){
        final String uri = url+"/user/updatePassword";
//        HttpEntity<Users> entity = new HttpEntity<>(users,HeaderJwt.getHeader());
        HttpEntity<Users> entity = new HttpEntity<>(users);
        restTemplate.put(uri,entity);
    }


    public void update(Users users){
        final String uri = url+"/user/update";
        HttpEntity<Users> entity = new HttpEntity<>(users,HeaderJwt.getHeader());
        restTemplate.put(uri,entity);
    }

    public Users findByLogin(String login){
        final String uri = url+"/user/findByLogin/{login}";
        Map<String,String> params = new HashMap<>();
        params.put("login",login);
        HttpEntity<Users> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users.class,params);

        return response.getBody();
    }

    public Users findByAdUser(String adUser){
        final String uri = url+"/user/findByAdUser/{aduser}";
        Map<String,String> params = new HashMap<>();
        params.put("aduser",adUser);
        HttpEntity<Users> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users.class,params);

        return response.getBody();
    }

    public List<Users> findByRol(String rol){
        final String uri = url+"/user/findByRol/{rol}";
        Map<String,String> params = new HashMap<>();
        params.put("rol",rol);
        HttpEntity<Users[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users[].class,params);

        return Arrays.asList(response.getBody());
    }

//    public String getPassword(String password){
//        final String uri = "http://localhost:8080/rest/user/v1/getPassword";
//        HttpHeaders headers =HeaderJwt.getHeader();
//        headers.set("pass", password);
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(uri,HttpMethod.GET,entity,String.class);
//        return response.getBody();
//    }




//    public List<UsersOfficeDto> getByUserOfficeByCityAndRol(String city, String rol){
//        final String uri = "http://localhost:8080/rest/userOffice/v1/getByCityAndRol/{city}/{rol}";
//        Map<String,String> params = new HashMap<>();
//        params.put("city",city);
//        params.put("rol",rol);
//        HttpEntity<UsersOfficeDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
//        ResponseEntity<UsersOfficeDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,UsersOfficeDto[].class,params);
//
//        return Arrays.asList(response.getBody());
//    }
//
//    public List<UsersOfficeDto> getByUserOfficeByRol(String rol){
//        final String uri = "http://localhost:8080/rest/userOffice/v1/getByRol/{rol}";
//        Map<String,String> params = new HashMap<>();
//        params.put("rol",rol);
//        HttpEntity<UsersOfficeDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
//        ResponseEntity<UsersOfficeDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,UsersOfficeDto[].class,params);
//
//        return Arrays.asList(response.getBody());
//    }
//
//    public List<UsersOfficeDto> getByInternalCodeOffice(String codeoffice){
//        final String uri = "http://localhost:8080/rest/userOffice/v1/getByInternalCodeOffice/{codeoffice}";
//        Map<String,String> params = new HashMap<>();
//        params.put("codeoffice",codeoffice);
//        HttpEntity<UsersOfficeDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
//        ResponseEntity<UsersOfficeDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,UsersOfficeDto[].class,params);
//
//        return Arrays.asList(response.getBody());
//    }
}
