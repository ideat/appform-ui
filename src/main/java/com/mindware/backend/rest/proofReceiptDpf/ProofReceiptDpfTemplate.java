package com.mindware.backend.rest.proofReceiptDpf;

import com.mindware.backend.util.HeaderJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProofReceiptDpfTemplate {
    @Value("${url}")
    private String url;

    @Value("${temp_file}")
    private String tempPath;

    RestTemplate restTemplate = new RestTemplate();

    public byte[] proofReceiptDpf(String pfmdpndep){
        final String uri = url + "/proofreceiptdpf";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("pfmdpndep",pfmdpndep);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);

        return response.getBody();
    }
}
