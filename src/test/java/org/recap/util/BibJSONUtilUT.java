package org.recap.util;

import info.freelibrary.marc4j.impl.ControlFieldImpl;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class BibJSONUtilUT extends BaseTestCaseUT {

    @InjectMocks
    BibJSONUtil bibJSONUtil;

    @Mock
    MarcUtil marcUtil;

    @Mock
    Record record;

    @Mock
    DataField dataField;

    @Mock
    ControlFieldImpl controlField;

    @Mock
    Subfield subfield;

    @Mock
    ProducerTemplate producerTemplate;

    @BeforeEach
    public void setup() {
        bibJSONUtil.setNonHoldingInstitutions(Arrays.asList("PUL"));
        bibJSONUtil.setProducerTemplate(producerTemplate);
        bibJSONUtil.getNonHoldingInstitutions();
        bibJSONUtil.getProducerTemplate();
    }

    @Test
    public void getLCCNValue() {
        Mockito.lenient().when(marcUtil.getDataFieldValue(any(), any(), any(), any(), any())).thenReturn("test");
        String result = bibJSONUtil.getLCCNValue(record);
        assertNotNull(result);
    }

    @Test
    public void getISBNNumber() {
        List<String> isbnNumberList = new ArrayList<>();
        isbnNumberList.add("233");
        Mockito.lenient().when(marcUtil.getMultiDataFieldValues(any(), anyString(), any(), any(), anyString())).thenReturn(isbnNumberList);
        List<VariableField> dataFields = new ArrayList<>();
        dataFields.add(dataField);
        Mockito.when(record.getVariableFields(anyString())).thenReturn(dataFields);
        Mockito.when(dataField.getSubfields(anyString())).thenReturn(Arrays.asList(subfield));
        Mockito.when(subfield.getData()).thenReturn("isbnNumbers");
        List<String> result = bibJSONUtil.getISBNNumber(record);
        assertNotNull(result);
    }

    @Test
    public void getISSNNumber() {
        List<String> isbnNumberList = new ArrayList<>();
        isbnNumberList.add("233");
        Mockito.lenient().when(marcUtil.getMultiDataFieldValues(any(), anyString(), any(), any(), anyString())).thenReturn(isbnNumberList);;
        List<VariableField> dataFields = new ArrayList<>();
        dataFields.add(dataField);
        Mockito.when(record.getVariableFields(anyString())).thenReturn(dataFields);
        Mockito.when(dataField.getSubfields(anyString())).thenReturn(Arrays.asList(subfield));
        Mockito.when(subfield.getData()).thenReturn("issnNumbers");
        List<String> result = bibJSONUtil.getISSNNumber(record);
        assertNotNull(result);
    }

    @Test
    public void getTitle() {
        String result = bibJSONUtil.getTitle(record);
        assertNotNull(result);
    }

    @Test
    public void getOCLCNumbers() {
        String institutionCode = "PUL";
        List<String> oclcNumberList = new ArrayList<>();
        oclcNumberList.add("OCoLC");
        List<String> nonHoldingInstitutions = new ArrayList<>();
        nonHoldingInstitutions.add("PUL");
        List<VariableField> dataFields = new ArrayList<>();
        dataFields.add(dataField);
        Mockito.when(record.getVariableFields(anyString())).thenReturn(dataFields);
        Mockito.when(dataField.getSubfields(anyString())).thenReturn(Arrays.asList(subfield));
        Mockito.when(subfield.getData()).thenReturn("OCoLC");
        ReflectionTestUtils.setField(bibJSONUtil, "nonHoldingInstitutions", nonHoldingInstitutions);
        List<String> result = bibJSONUtil.getOCLCNumbers(record, institutionCode);
        assertNotNull(result);
    }

    @Test
    public void getOCLCNumbersForControlFieldValue() {
        String institutionCode = "PUL";
        List<String> oclcNumberList = new ArrayList<>();
        oclcNumberList.add("OCoLC");
        List<String> nonHoldingInstitutions = new ArrayList<>();
        nonHoldingInstitutions.add("PUL");
        List<VariableField> dataFields = new ArrayList<>();
        dataFields.add(controlField);
        Mockito.lenient().when(record.getVariableFields("003")).thenReturn(dataFields);
        Mockito.lenient().when(record.getVariableFields("001")).thenReturn(dataFields);
        Mockito.when(controlField.getData()).thenReturn("OCoLC");
        ReflectionTestUtils.setField(bibJSONUtil, "nonHoldingInstitutions", nonHoldingInstitutions);
        List<String> result = bibJSONUtil.getOCLCNumbers(record, institutionCode);
        assertNotNull(result);
    }
}
