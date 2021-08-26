package org.recap.service.statusreconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.util.PropertyUtil;


public class StatusReconciliationEmailServiceUT extends BaseTestCaseUT {

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

    @Mock
    PropertyUtil propertyUtil;

    @Test
    public void processInput(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "StatusReconciliationFile");
        statusReconciliationEmailService.processInput(exchange);
    }

    @Test
    public void processInputForFailure(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "StatusReconciliationFailureFile");
        statusReconciliationEmailService.processInputForFailure(exchange);
    }
}
