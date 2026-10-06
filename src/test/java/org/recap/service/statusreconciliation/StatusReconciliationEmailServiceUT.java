package org.recap.service.statusreconciliation;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
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
    public void processInput() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "DailyReconciliationFile");
        exchange.getIn().setHeader(ScsbConstants.CHANGED_TO_AVAILABLE, 1l);
        exchange.getIn().setHeader(ScsbConstants.UNCHANGED, 1l);
        exchange.getIn().setHeader(ScsbConstants.UNKNOWN_CODE, 1l);
        statusReconciliationEmailService.processInput(exchange);
    }


    @Test
    public void processInputForFailure() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "DailyReconciliationFile");
        exchange.getIn().setHeader(ScsbConstants.CHANGED_TO_AVAILABLE, 1l);
        exchange.getIn().setHeader(ScsbConstants.UNCHANGED, 1l);
        exchange.getIn().setHeader(ScsbConstants.UNKNOWN_CODE, 1l);
        exchange.getIn().setHeader(ScsbConstants.FAILED, 1);
        statusReconciliationEmailService.processInputForFailure(exchange);
    }

}
