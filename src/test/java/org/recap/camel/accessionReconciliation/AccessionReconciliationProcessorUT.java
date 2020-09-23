package org.recap.camel.accessionReconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.accessionreconciliation.AccessionReconciliationProcessor;
import org.recap.camel.accessionreconciliation.BarcodeReconcilitaionReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
public class AccessionReconciliationProcessorUT extends BaseTestCaseUT {
    @InjectMocks
    AccessionReconciliationProcessor mockedAccessionReconciliationProcessor;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    RestTemplate restTemplate;
    @Value("${scsb.solr.client.url}")
    private String solrSolrClientUrl;

    @Value("${accession.reconciliation.filePath}")
    private String accessionFilePath;

    @Before
    public  void setup(){
        ReflectionTestUtils.setField(mockedAccessionReconciliationProcessor, "solrSolrClientUrl", solrSolrClientUrl);
        ReflectionTestUtils.setField(mockedAccessionReconciliationProcessor, "accessionFilePath", accessionFilePath);
    }

    @AfterTestMethod
    public void finall() throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.BARCODE_RECONCILIATION_FILE_DATE_FORMAT);
        Path filePathPul = Paths.get(accessionFilePath+ RecapCommonConstants.PATH_SEPARATOR+"pul"+RecapCommonConstants.PATH_SEPARATOR+ RecapConstants.ACCESSION_RECONCILATION_FILE_NAME+"pul"+simpleDateFormat.format(new Date())+".csv");
        Path filePathCul = Paths.get(accessionFilePath+ RecapCommonConstants.PATH_SEPARATOR+"cul"+RecapCommonConstants.PATH_SEPARATOR+ RecapConstants.ACCESSION_RECONCILATION_FILE_NAME+"cul"+simpleDateFormat.format(new Date())+".csv");
        Path filePathNypl = Paths.get(accessionFilePath+ RecapCommonConstants.PATH_SEPARATOR+"nypl"+RecapCommonConstants.PATH_SEPARATOR+ RecapConstants.ACCESSION_RECONCILATION_FILE_NAME+"nypl"+simpleDateFormat.format(new Date())+".csv");
        boolean deletedPul = Files.deleteIfExists(filePathPul);
        Assert.assertTrue(deletedPul);
        boolean deletedCul = Files.deleteIfExists(filePathCul);
        Assert.assertTrue(deletedCul);
        boolean deletedNypl = Files.deleteIfExists(filePathNypl);
        Assert.assertTrue(deletedNypl);

    }
    @Test
    public void processInput(){
        BarcodeReconcilitaionReport barcodeReconcilitaionReport = getBarcodeReconcilitaionReport();
        ArrayList<BarcodeReconcilitaionReport> barcodeReconcilitaionReports = new ArrayList<>();
        barcodeReconcilitaionReports.add(0,barcodeReconcilitaionReport);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = getExchange(barcodeReconcilitaionReports, ctx);
        ex.setProperty(RecapConstants.CAMEL_SPLIT_INDEX,1);
        Map<String, Boolean> map = new HashMap<>();
        map.put("accessionReconcilationService", true);
        ReflectionTestUtils.setField(mockedAccessionReconciliationProcessor, "institutionCode", "pul");
        ReflectionTestUtils.setField(mockedAccessionReconciliationProcessor, "camelContext", ctx);
        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
        HashMap<String,String> barcodesAndCustomerCodes=new HashMap<>();
        barcodesAndCustomerCodes.put(barcodeReconcilitaionReport.getBarcode(),barcodeReconcilitaionReport.getCustomerCode());
        HttpEntity httpEntity = new HttpEntity(barcodesAndCustomerCodes);
        Mockito.when(restTemplate.exchange(solrSolrClientUrl+ RecapConstants.ACCESSION_RECONCILATION_SOLR_CLIENT_URL, HttpMethod.POST, httpEntity,Map.class)).thenReturn(responseEntity);
        mockedAccessionReconciliationProcessor.processInput(ex);
    }
    @Test
    public void processInputWithoutIndex(){
        BarcodeReconcilitaionReport barcodeReconcilitaionReport = getBarcodeReconcilitaionReport();
        ArrayList<BarcodeReconcilitaionReport> barcodeReconcilitaionReports = new ArrayList<>();
        barcodeReconcilitaionReports.add(0,barcodeReconcilitaionReport);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = getExchange(barcodeReconcilitaionReports, ctx);
        ex.setProperty(RecapConstants.CAMEL_SPLIT_INDEX,0);
        Map<String, Boolean> map = new HashMap<>();
        map.put("accessionReconcilationService", true);
        ReflectionTestUtils.setField(mockedAccessionReconciliationProcessor, "institutionCode", "cul");
        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
        HashMap<String,String> barcodesAndCustomerCodes=new HashMap<>();
        barcodesAndCustomerCodes.put(barcodeReconcilitaionReport.getBarcode(),barcodeReconcilitaionReport.getCustomerCode());
        HttpEntity httpEntity = new HttpEntity(barcodesAndCustomerCodes);
        Mockito.when(restTemplate.exchange(solrSolrClientUrl+ RecapConstants.ACCESSION_RECONCILATION_SOLR_CLIENT_URL, HttpMethod.POST, httpEntity,Map.class)).thenReturn(responseEntity);
        mockedAccessionReconciliationProcessor.processInput(ex);
        ReflectionTestUtils.setField(mockedAccessionReconciliationProcessor, "camelContext", ctx);
        ReflectionTestUtils.setField(mockedAccessionReconciliationProcessor, "institutionCode", "nypl");
        mockedAccessionReconciliationProcessor.processInput(ex);
    }

    private Exchange getExchange(ArrayList<BarcodeReconcilitaionReport> barcodeReconcilitaionReports, CamelContext ctx) {
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        ex.setIn(in);
        ex.setProperty(RecapConstants.CAMEL_SPLIT_COMPLETE,true);
        in.setBody(barcodeReconcilitaionReports);
        return ex;
    }

    private BarcodeReconcilitaionReport getBarcodeReconcilitaionReport() {
        BarcodeReconcilitaionReport barcodeReconcilitaionReport = new BarcodeReconcilitaionReport();
        barcodeReconcilitaionReport.setBarcode("123456");
        barcodeReconcilitaionReport.setCustomerCode("PA");
        barcodeReconcilitaionReport.setStatus("SUCCESS");
        return barcodeReconcilitaionReport;
    }
}
