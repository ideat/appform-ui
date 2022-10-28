package com.mindware.backend.rest.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginRestTemplate {
    @Value("${url}")
    private String url;

    @Value("${url-ldap}")
    private String urlLdap;

    private static final RestTemplate restTemplate = new RestTemplate();

    public Token getToken(JwtRequest jwtRequest){
        final String uri = url +"/authenticate";
//        Token token = new Token();
        HttpEntity<JwtRequest> entity = new HttpEntity<>(jwtRequest);
        ResponseEntity<Token> response = restTemplate.postForEntity(uri,entity,Token.class);
        return response.getBody();
    }

    public Token getTokenLdap(JwtRequest jwtRequest){
        final String uri = urlLdap +"/login";

        HttpEntity<JwtRequest> entity = new HttpEntity<>(jwtRequest);
        ResponseEntity<Token> response = restTemplate.postForEntity(uri,entity,Token.class);
        return response.getBody();
    }
}
