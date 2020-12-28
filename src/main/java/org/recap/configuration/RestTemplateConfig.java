package org.recap.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Component
@Slf4j
public class RestTemplateConfig {
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Value("${bibdata.api.connection.timeout}")
    Integer connectionTimeout;

    @Value("${bibdata.api.read.timeout}")
    Integer readTimeout;


    @Autowired
    public RestTemplateConfig(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    public HttpHeaders getHttpHeaders(String institution) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        return headers;
    }

    public HttpEntity getHttpEntity(HttpHeaders headers, String institution) {
        return new HttpEntity(headers);
    }

    public String getForString(String institution, String url, Object... uriVariables) {
        try {
            String response;
            HttpHeaders header = getHttpHeaders(institution);
            HttpEntity requestEntity = getHttpEntity(header, institution);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, uriVariables);
            response = responseEntity.getBody();
            return response;
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("No data found {}", url);
            } else {
                log.info("rest client exception", exception.getMessage());
            }
            String response = RecapConstants.ITEM_BARCODE_NOT_FOUND + url + exception.getMessage();

            throw new RuntimeException(response);
        }
    }

    public SimpleClientHttpRequestFactory getClientHttpRequestFactory()
    {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(10_000);
        //Read timeout
        clientHttpRequestFactory.setReadTimeout(10_000);
        return clientHttpRequestFactory;
    }

}
