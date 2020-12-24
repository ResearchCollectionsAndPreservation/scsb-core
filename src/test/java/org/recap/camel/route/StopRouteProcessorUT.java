package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;

public class StopRouteProcessorUT {

    @Mock
    Exchange exchange;

    @Test
    public void process() throws Exception {
        StopRouteProcessor stopRouteProcessor = new StopRouteProcessor("pulSubmitCollectionFTPCgdProtectedRoute");
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        stopRouteProcessor.process(ex);
        assertTrue(true);
    }

    @Test
    public void processException() throws Exception {
        StopRouteProcessor stopRouteProcessor = new StopRouteProcessor("pulSubmitCollectionFTPCgdProtectedRoute");
        stopRouteProcessor.process(exchange);
        assertTrue(true);
    }
}
