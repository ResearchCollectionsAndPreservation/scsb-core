package org.recap.service.partnerservice;

import org.recap.RecapConstants;
import org.recap.service.authorization.NyplOauthTokenApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by premkb on 18/12/16.
 */
@Service
public class NYPLService {

    private static final Logger logger = LoggerFactory.getLogger(NYPLService.class);

    @Value("${ils.nypl.bibdata}")
    private String ilsNYPLBibData;

    @Value("${bibdata.api.connection.timeout}")
    private Integer connectionTimeout;

    @Value("${bibdata.api.read.timeout}")
    private Integer readTimeout;

    @Value("${ils.nypl.bibdata.parameter}")
    private String ilsNYPLBibDataParameter;

    @Autowired
    private NyplOauthTokenApiService nyplOauthTokenApiService;

    public NyplOauthTokenApiService getNyplOauthTokenApiService() {
        return nyplOauthTokenApiService;
    }

    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public String getIlsNYPLBibData() {
        return ilsNYPLBibData;
    }

    public String getIlsNYPLBibDataParameter() {
        return ilsNYPLBibDataParameter;
    }

    public HttpEntity getHttpEntity(HttpHeaders headers){
        return new HttpEntity(headers);
    }

    public HttpHeaders getHttpHeaders(){
        return new HttpHeaders();
    }

    /**
     * This method gets bib data response(scsb xml) based on the itemBarcode and customer code from ILS for NYPL.
     *
     * @param itemBarcode  the item barcode
     * @param customerCode the customer code
     * @return the bib data
     */
    public String getBibData(String itemBarcode, String customerCode) {
        RestTemplate restTemplate = getRestTemplate();
        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(connectionTimeout);
        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setReadTimeout(readTimeout);
        String bibDataResponse;
        String response;
        try {
            String authorization = "Bearer " + getNyplOauthTokenApiService().generateAccessTokenForNyplApi();
            HttpHeaders headers = getHttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.set("Authorization", authorization);
            HttpEntity requestEntity = getHttpEntity(headers);
            Map<String, String> params  = new HashMap<>();
            params.put("barcode", itemBarcode);
            params.put("customercode", customerCode);
            String url = getIlsNYPLBibData() + getIlsNYPLBibDataParameter();
            logger.info("NYPL BIBDATA URL = {}",url);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, params);
            bibDataResponse = responseEntity.getBody();
        } catch (Exception e) {
            response = String.format("[%s : %s] %s. (%s : %s)", itemBarcode, customerCode, RecapConstants.ITEM_BARCODE_NOT_FOUND, ilsNYPLBibData, e.getMessage());
            logger.error(response);
            throw new RuntimeException(response);
        }
        return bibDataResponse;
    }

}
