package org.recap.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.model.jpa.ItemEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

public class SolrDocIndexServiceUT extends BaseTestCaseUT {

    @InjectMocks
    SolrDocIndexService solrDocIndexService;

    @Mock
    ItemEntity itemEntity;

    @Mock
    RestHeaderService restHeaderService;

    @Value("${" + PropertyKeyConstants.SCSB_SOLR_DOC_URL + "}")
    private String scsbSolrDocUrl;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(solrDocIndexService, "scsbSolrDocUrl", scsbSolrDocUrl);
    }

    @Test
    public void updateSolrIndexException() {
        Mockito.when(itemEntity.getBarcode()).thenReturn("123456");
        solrDocIndexService.updateSolrIndex(itemEntity);
    }
}
