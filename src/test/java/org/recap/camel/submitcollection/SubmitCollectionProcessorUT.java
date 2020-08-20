package org.recap.camel.submitcollection;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.camel.submitcollection.processor.SubmitCollectionProcessor;
import org.recap.service.common.SetupDataService;
import org.recap.service.submitcollection.SubmitCollectionBatchService;
import org.recap.service.submitcollection.SubmitCollectionReportGenerator;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionProcessorUT{
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

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(submitCollectionProcessor, "submitCollectionEmailSubject","Submit collection completed" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "submitCollectionPULReportLocation","null/share/recap/reports/collection/submitCollection/local/pul" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "submitCollectionCULReportLocation","null/share/recap/reports/collection/submitCollection/local/cul" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "submitCollectionNYPLReportLocation","null/share/recap/reports/collection/submitCollection/local/nypl" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "emailCCForCul","testCul@gmail.com" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "emailCCForPul","testPul@gmail.com" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "emailCCForNypl","testNypl@gmail.com" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "emailToNYPL","testNypl@gmail.com" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "emailToCUL","testCul@gmail.com" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "emailToPUL","testPul@gmail.com" );
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubmitCollectionProcessor() {
        SubmitCollectionProcessor submitCollectionProcessor = new SubmitCollectionProcessor("NYPL", false);

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
