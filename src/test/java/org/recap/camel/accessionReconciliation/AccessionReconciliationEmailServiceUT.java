package org.recap.camel.accessionReconciliation;

import org.apache.camel.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.camel.accessionreconciliation.AccessionReconciliationEmailService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

/**
 * Created by akulak on 25/5/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessionReconciliationEmailServiceUT {

    @InjectMocks
    AccessionReconciliationEmailService accessionReconciliationEmailService;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    Header dataheader;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    EmailPayLoad emailPayLoad;

    String emailAddress = "test@mail.com";

    String ccEmailAddress = "testcc@mail.com";

    @Before
    public  void setup(){
        ReflectionTestUtils.setField(accessionReconciliationEmailService,"pulEmailTo",emailAddress);
        ReflectionTestUtils.setField(accessionReconciliationEmailService,"pulEmailCC",ccEmailAddress);
    }

    @Test
    public void testEmailIdTo() throws Exception{
        String institution = "PUL";
        AccessionReconciliationEmailService accessionReconciliationEmailService = new AccessionReconciliationEmailService(institution);
        String result = accessionReconciliationEmailService.emailIdTo(institution, emailPayLoad);
        assertNull(result);
    }
    @Test
    public void getEmailPayLoad(){
        String institutionCode = "NYPL";
        AccessionReconciliationEmailService accessionReconciliationEmailService1 = new AccessionReconciliationEmailService(institutionCode);
        emailPayLoad.setTo(emailAddress);
        emailPayLoad.setCc(ccEmailAddress);
        message.setHeader("CamelFileNameProduced",dataheader);
        Mockito.when(exchange.getIn()).thenReturn(message);
        EmailPayLoad emailPayLoad = accessionReconciliationEmailService1.getEmailPayLoad(exchange);
        assertNotNull(emailPayLoad);
    }

    /*@Test
    public void processInput(){
        producerTemplate.setThreadedAsyncMode(true);
        producerTemplate.setDefaultEndpointUri("endpoint.com");
        producerTemplate.setEventNotifierEnabled(true);
        AccessionReconciliationEmailService accessionReconciliationEmailService1 = new AccessionReconciliationEmailService("CUL",producerTemplate);
        message.setHeader("CamelFileNameProduced",dataheader);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.doNothing().when(producerTemplate).sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.EMAIL_BODY_FOR, "AccessionReconcilation");
        accessionReconciliationEmailService1.processInput(exchange);
    }*/

}
