package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class BibJSONUtilUT extends BaseTestCaseUT {

    @InjectMocks
    BibJSONUtil bibJSONUtil;

    @Mock
    MarcUtil marcUtil;

    @Mock
    Record record ;

    @Mock
    ProducerTemplate producerTemplate;

    @Before
    public void setup(){
        bibJSONUtil.setNonHoldingInstitutions(Arrays.asList("PUL"));
        bibJSONUtil.setProducerTemplate(producerTemplate);
        bibJSONUtil.getNonHoldingInstitutions();
        bibJSONUtil.getProducerTemplate();
    }

    @Test
    public void getLCCNValue(){
        Mockito.when(marcUtil.getDataFieldValue(any(), any(), any(), any(), any())).thenReturn("test");
        String result = bibJSONUtil.getLCCNValue(record);
        assertNotNull(result);
    }

    @Test
    public void getISBNNumber(){
        List<String> isbnNumberList = new ArrayList<>();
        isbnNumberList.add("233");
        Mockito.when(marcUtil.getMultiDataFieldValues(any(), anyString(), any(), any(), anyString())).thenReturn(isbnNumberList);
        List<String> result = bibJSONUtil.getISBNNumber(record);
        assertNotNull(result);
    }

    @Test
    public void getISSNNumber(){
        List<String> isbnNumberList = new ArrayList<>();
        isbnNumberList.add("233");
        Mockito.when(marcUtil.getMultiDataFieldValues(any(), anyString(), any(), any(), anyString())).thenReturn(isbnNumberList);
        List<String> result = bibJSONUtil.getISSNNumber(record);
        assertNotNull(result);
    }

    @Test
    public void getTitle(){
        String result = bibJSONUtil.getTitle(record);
        assertNotNull(result);
    }

    @Test
    public void getOCLCNumbers(){
        String institutionCode = "PUL";
        List<String> oclcNumberList = new ArrayList<>();
        oclcNumberList.add("OCoLC");
        List<String> nonHoldingInstitutions = new ArrayList<>();
        nonHoldingInstitutions.add("PUL");
        ReflectionTestUtils.setField(bibJSONUtil,"nonHoldingInstitutions",nonHoldingInstitutions);
        List<String> result = bibJSONUtil.getOCLCNumbers(record,institutionCode);
        assertNotNull(result);
    }
}
