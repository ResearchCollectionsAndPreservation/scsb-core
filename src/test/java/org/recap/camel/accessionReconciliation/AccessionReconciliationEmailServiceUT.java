package org.recap.camel.accessionReconciliation;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.camel.accessionreconciliation.AccessionReconciliationEmailService;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
/**
 * Created by akulak on 25/5/17.
 */

public class AccessionReconciliationEmailServiceUT extends BaseTestCaseUT {

    @InjectMocks
    AccessionReconciliationEmailService accessionReconciliationEmailService;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ProducerTemplate producerTemplate;

    String institutionCode = "CUL";

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
     }

    @Test
    public void processInput(){
        ReflectionTestUtils.setField(accessionReconciliationEmailService,"institutionCode",institutionCode);
        message.setHeader("CamelFileNameProduced","AccessionReconciliationFile");
        exchange.setIn(message);
        Mockito.when(exchange.getIn()).thenReturn(message);
        accessionReconciliationEmailService.processInput(exchange);
        assertEquals("CUL",institutionCode);
    }

}
