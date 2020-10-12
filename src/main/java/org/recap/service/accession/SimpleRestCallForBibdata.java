package org.recap.service.accession;

import org.recap.RecapConstants;
import org.recap.service.partnerservice.NullHostnameVerifier;
import org.recap.service.partnerservice.SCSBSimpleClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SimpleRestCallForBibdata extends BibDataAbstract{

    private static final Logger logger = LoggerFactory.getLogger(SimpleRestCallForBibdata.class);

    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
    public  RestTemplate getRestTmp() {
        RestTemplate restTmp = getRestTemplate();
        HostnameVerifier verifier = new NullHostnameVerifier();
        SCSBSimpleClientHttpRequestFactory factory = new SCSBSimpleClientHttpRequestFactory(verifier);
        restTmp.setRequestFactory(factory);
        ((SimpleClientHttpRequestFactory) restTmp.getRequestFactory()).setConnectTimeout(connectionTimeout);
        ((SimpleClientHttpRequestFactory) restTmp.getRequestFactory()).setReadTimeout(readTimeout);
        return restTmp;
    }

    @Override
    public String getBibData(String itemBarcode, String customerCode, String institution,String url) {
        String bibDataResponse;
        String response;
        try {
            logger.info("BIBDATA URL = {}" , url);
            Map<String, String> params = getParamsMap(itemBarcode);
            RestTemplate restTmp = getRestTmp();
            HttpEntity requestEntity = new HttpEntity(getHttpHeaders());
            ResponseEntity<String> responseEntity = restTmp.exchange(url, HttpMethod.GET, requestEntity, String.class, params);
            bibDataResponse = responseEntity.getBody();
        } catch (Exception e) {
            response = String.format("[%s] %s. (%s : %s)", itemBarcode, RecapConstants.ITEM_BARCODE_NOT_FOUND, url, e.getMessage());
            logger.error(response);
            throw new RuntimeException(response);
        }
        return bibDataResponse;
    }



    public Map<String, String> getParamsMap(String itemBarcode) {
        Map<String, String> params = new HashMap<>();
        params.put("barcode", itemBarcode);
        return params;
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        return headers;
    }

    @Override
    public boolean isAuth(String auth) {
        return "NoAuth".equalsIgnoreCase(auth);
    }
}
