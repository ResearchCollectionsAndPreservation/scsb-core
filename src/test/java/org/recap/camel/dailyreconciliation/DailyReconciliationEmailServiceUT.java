package org.recap.camel.dailyreconciliation;


import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class DailyReconciliationEmailServiceUT {

    @InjectMocks
    DailyReconciliationEmailService dailyReconciliationEmailService;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    Header dataheader;


    String emailAddress = "test@mail.com";

    @Before
    public  void setup(){
        ReflectionTestUtils.setField(dailyReconciliationEmailService,"emailTo",emailAddress);
    }

    @Test
    public void process() throws Exception {
        message.setHeader("CamelFileNameProduced",dataheader);
        exchange.setIn(message);
        Mockito.when(exchange.getIn()).thenReturn(message);
        dailyReconciliationEmailService.process(exchange);
    }
}
