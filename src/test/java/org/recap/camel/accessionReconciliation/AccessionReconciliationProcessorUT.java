package org.recap.camel.accessionReconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapConstants;
import org.recap.camel.accessionreconciliation.AccessionReconciliationProcessor;
import org.recap.camel.accessionreconciliation.BarcodeReconcilitaionReport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AccessionReconciliationProcessorUT {
    @InjectMocks
    AccessionReconciliationProcessor mockedAccessionReconciliationProcessor;

    @Mock
    CamelContext mockedCamelContext;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    HttpEntity httpEntity;

    @Mock
    RestTemplate restTemplate;

    private String solrSolrClientUrl = "http://localhost:9090/";

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processInput(){
        BarcodeReconcilitaionReport barcodeReconcilitaionReport = new BarcodeReconcilitaionReport();
        barcodeReconcilitaionReport.setBarcode("123456");
        barcodeReconcilitaionReport.setCustomerCode("PA");
        barcodeReconcilitaionReport.setStatus("SUCCESS");
        ArrayList<BarcodeReconcilitaionReport> barcodeReconcilitaionReports = new ArrayList<>();
        barcodeReconcilitaionReports.add(0,barcodeReconcilitaionReport);

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(barcodeReconcilitaionReports);
        ex.setIn(in);
        message.setMessageId("1");
        message.setBody(barcodeReconcilitaionReports, ArrayList.class);
        exchange.setProperty(RecapConstants.CAMEL_SPLIT_INDEX,1);
        Map<String, Boolean> map = new HashMap<>();
        map.put("accessionReconcilationService", true);
        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
//        Mockito.when(exchange.getProperty(RecapConstants.CAMEL_SPLIT_INDEX)).thenReturn(1);
       /* Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any())).thenReturn(responseEntity);*/
//        mockedAccessionReconciliationProcessor.processInput(ex);
    }
}
