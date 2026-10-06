package org.recap.service.accession;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.service.authorization.OauthTokenApiService;
import org.recap.util.PropertyUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OAuthRestCallForBibdataUT extends BaseTestCaseUT {

    @Mock
    OAuthRestCallForBibdata OAuthRestCallForBibdata;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    RestTemplate restTemplate;

    @Mock
    OauthTokenApiService oauthTokenApiService;

    @Mock
    SimpleClientHttpRequestFactory factory;


    @Test
    public void getBibData() throws Exception {
        ReflectionTestUtils.setField(OAuthRestCallForBibdata, "propertyUtil", propertyUtil);
        ReflectionTestUtils.setField(OAuthRestCallForBibdata, "connectionTimeout", 20000);
        ReflectionTestUtils.setField(OAuthRestCallForBibdata, "readTimeout", 20000);
        ReflectionTestUtils.setField(OAuthRestCallForBibdata, "oauthTokenApiService", oauthTokenApiService);
        Mockito.when(OAuthRestCallForBibdata.getParamsMap(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(OAuthRestCallForBibdata.getRestTmp()).thenCallRealMethod();
        Mockito.when(OAuthRestCallForBibdata.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.getRequestFactory()).thenReturn(factory);
        Mockito.doNothing().when(factory).setConnectTimeout(Mockito.anyInt());
        Mockito.doNothing().when(factory).setReadTimeout(Mockito.anyInt());
        Mockito.when(oauthTokenApiService.generateAccessToken(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("test");
        ResponseEntity<String> responseEntity = new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(getHttpHeaders("test", "1", "fdhfujfdjfsd"));
        Mockito.when(restTemplate.exchange("url", HttpMethod.GET, requestEntity, String.class, getParamsMap("123456", "PA"))).thenReturn(responseEntity);
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(getIlsConfigProperties());
        Mockito.when(OAuthRestCallForBibdata.getHttpHeaders(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(OAuthRestCallForBibdata.getBibData("123456", "PA", "PUL", "url")).thenCallRealMethod();
        String bibDataResponse = OAuthRestCallForBibdata.getBibData("123456", "PA", "PUL", "url");
        assertEquals(ScsbCommonConstants.SUCCESS, bibDataResponse);
    }

    @Test
    public void testgetRestTemplate() {
        Mockito.when(OAuthRestCallForBibdata.getRestTemplate()).thenCallRealMethod();
        OAuthRestCallForBibdata.getRestTemplate();
        assertTrue(true);
    }

    public HttpHeaders getHttpHeaders(String oauthTokenApiUrl, String operatorUserId, String operatorPassword) throws Exception {
        String authorization = "Bearer " + oauthTokenApiService.generateAccessToken(oauthTokenApiUrl, operatorUserId, operatorPassword);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        headers.set("Authorization", authorization);
        return headers;
    }

    public Map<String, String> getParamsMap(String itemBarcode, String customerCode) {
        Map<String, String> params = new HashMap<>();
        params.put("barcode", itemBarcode);
        params.put("customercode", customerCode);
        return params;
    }

    private ILSConfigProperties getIlsConfigProperties() {
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setOauthTokenApiUrl("test");
        ilsConfigProperties.setOperatorUserId("1");
        ilsConfigProperties.setOperatorPassword("fdhfujfdjfsd");
        return ilsConfigProperties;
    }
}
