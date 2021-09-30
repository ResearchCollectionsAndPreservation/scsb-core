package org.recap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.configuration.RestTemplateConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestTemplateConfigUT extends BaseTestCaseUT {

    @InjectMocks
    RestTemplateConfig restTemplateConfig;

    @Mock
    RestTemplateBuilder restTemplateBuilder;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    RestTemplate restTemplate;

    @Test
    public void getForString() {
        Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
        ReflectionTestUtils.setField(restTemplateConfig,"restTemplate",restTemplate);
        ReflectionTestUtils.setField(restTemplateConfig,"objectMapper",objectMapper);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK);
        Mockito.doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any(),
                ArgumentMatchers.<Class>any());
        String response= restTemplateConfig.getForString(ScsbCommonConstants.PRINCETON,"url");
        assertTrue(response.contains(ScsbCommonConstants.SUCCESS));
    }

    @Test
    public void getForStringHttpClientErrorException() {
        Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
        ReflectionTestUtils.setField(restTemplateConfig,"restTemplate",restTemplate);
        ReflectionTestUtils.setField(restTemplateConfig,"objectMapper",objectMapper);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK);
        Mockito.doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any(),
                ArgumentMatchers.<Class>any());
        try {
            String response= restTemplateConfig.getForString(ScsbCommonConstants.PRINCETON,"url");
        }catch (RuntimeException exception){}

    }
    @Test
    public void getForStringException() {
        Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
        ReflectionTestUtils.setField(restTemplateConfig,"restTemplate",restTemplate);
        ReflectionTestUtils.setField(restTemplateConfig,"objectMapper",objectMapper);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK);
        Mockito.doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any(),
                ArgumentMatchers.<Class>any());
        try {
            String response= restTemplateConfig.getForString(ScsbCommonConstants.PRINCETON,"url");
        }catch (RuntimeException exception){}

    }

    @Test
    public void getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory=restTemplateConfig.getClientHttpRequestFactory();
        assertNotNull(clientHttpRequestFactory);
    }
}
