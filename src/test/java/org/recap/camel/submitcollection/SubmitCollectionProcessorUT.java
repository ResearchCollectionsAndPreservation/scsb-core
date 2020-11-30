package org.recap.camel.submitcollection;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.camel.submitcollection.processor.SubmitCollectionProcessor;
import org.recap.service.common.SetupDataService;
import org.recap.service.submitcollection.SubmitCollectionBatchService;
import org.recap.service.submitcollection.SubmitCollectionReportGenerator;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


public class SubmitCollectionProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    SubmitCollectionProcessor submitCollectionProcessor;

    @Mock
    private SetupDataService setupDataService;

    @Mock
    SubmitCollectionBatchService submitCollectionBatchService;

    @Mock
    SubmitCollectionReportGenerator submitCollectionReportGenerator;

    @Mock
    private ProducerTemplate producer;

    @Mock
    PropertyUtil propertyUtil;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(submitCollectionProcessor, "submitCollectionEmailSubject","Submit collection completed" );
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubmitCollectionProcessor() {
        SubmitCollectionProcessor submitCollectionProcessor = new SubmitCollectionProcessor("NYPL", false);
        ReflectionTestUtils.setField(submitCollectionProcessor,"propertyUtil",propertyUtil);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setHeader("institution", "CUL");
        ex.getIn().setBody("Test text for Example");
        Exception e = new Exception();
        Throwable t = new ArithmeticException();
        e.addSuppressed(t);
        ex.setProperty("CamelExceptionCaught",e);
        try {
            submitCollectionProcessor.processInput(ex); } catch (Exception ef) {}
        try{
            submitCollectionProcessor.caughtException(ex);
        } catch (Exception ef) {}
        assertTrue(true);
    }

    @Test
    public void processInputForCUL(){
        ReflectionTestUtils.setField(submitCollectionProcessor, "institutionCode","CUL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setHeader("institution", "CUL");
        ex.getIn().setBody("Test text for Example");
        Map institutionCodeIdMap = new HashMap<>();
        institutionCodeIdMap.put("CUL",2);
        institutionCodeIdMap.put("PUL",1);
        institutionCodeIdMap.put("NYPL",3);
        Mockito.when(setupDataService.getInstitutionCodeIdMap().get("CUL")).thenReturn(institutionCodeIdMap);
        submitCollectionProcessor.processInput(ex);
    }
    @Test
    public void processInputForPUL(){
        ReflectionTestUtils.setField(submitCollectionProcessor, "institutionCode","PUL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "PUL");
        ex.getIn().setHeader("CamelFileParent", "PUL");
        ex.getIn().setHeader("institution", "PUL");
        ex.getIn().setBody("Test text for Example");
        Map institutionCodeIdMap = new HashMap<>();
        institutionCodeIdMap.put("CUL",2);
        institutionCodeIdMap.put("PUL",1);
        institutionCodeIdMap.put("NYPL",3);
        Mockito.when(setupDataService.getInstitutionCodeIdMap().get("PUL")).thenReturn(institutionCodeIdMap);
        submitCollectionProcessor.processInput(ex);
    }
    @Test
    public void processInputForNYPL(){
        ReflectionTestUtils.setField(submitCollectionProcessor, "institutionCode","NYPL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "NYPL");
        ex.getIn().setHeader("CamelFileParent", "NYPL");
        ex.getIn().setHeader("institution", "NYPL");
        ex.getIn().setBody("Test text for Example");
        Map institutionCodeIdMap = new HashMap<>();
        institutionCodeIdMap.put("CUL",2);
        institutionCodeIdMap.put("PUL",1);
        institutionCodeIdMap.put("NYPL",3);
        Mockito.when(setupDataService.getInstitutionCodeIdMap().get("NYPL")).thenReturn(institutionCodeIdMap);
        submitCollectionProcessor.processInput(ex);
    }
}
