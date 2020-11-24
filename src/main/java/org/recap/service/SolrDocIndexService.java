package org.recap.service;

import lombok.extern.slf4j.Slf4j;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class SolrDocIndexService {

    @Value("${scsb.solr.doc.url}")
    private String scsbSolrDocUrl;

    @Autowired
    private RestHeaderService restHeaderService;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * Update solr index.
     *
     * @param itemEntity the item entity
     */
    public void updateSolrIndex(ItemEntity itemEntity) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrDocUrl + RecapConstants.UPDATE_ITEM_STATUS_SOLR).queryParam(RecapConstants.UPDATE_ITEM_STATUS_SOLR_PARAM_ITEM_ID, itemEntity.getBarcode());
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class);
            log.info(responseEntity.getBody());
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
    }
}
