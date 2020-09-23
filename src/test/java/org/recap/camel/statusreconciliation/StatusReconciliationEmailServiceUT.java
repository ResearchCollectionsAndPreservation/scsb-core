package org.recap.camel.statusreconciliation;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatusReconciliationEmailServiceUT {

    @InjectMocks
    StatusReconciliationEmailService statusReconciliationEmailService;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    Header dataheader;

    @Test
    public void processInput(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "DailyReconciliationFile");
        statusReconciliationEmailService.processInput(exchange);
    }
}
