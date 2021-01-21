package org.recap.service.authorization;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

/**
 * Created by rajeshbabuk on 9/12/16.
 */
@Service
public class OauthTokenApiService {

    /**
     * This method is used to generate token to access the Institution's API using the operator credentials if they are authorized.
     *
     * @return the string
     * @throws Exception the exception
     */
    public String generateAccessToken(String oauthTokenApiUrl, String operatorUserId, String operatorPassword) throws Exception {
        String authorization = "Basic " + new String(Base64Utils.encode((operatorUserId + ":" + operatorPassword).getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authorization);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity("grant_type=client_credentials", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(oauthTokenApiUrl, HttpMethod.POST, requestEntity, String.class);
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());
            return (String) jsonObject.get("access_token");
        }
}
