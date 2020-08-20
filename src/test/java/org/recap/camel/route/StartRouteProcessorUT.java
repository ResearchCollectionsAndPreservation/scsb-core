package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class StartRouteProcessorUT {
    @Test
    public void process() throws Exception {
        StartRouteProcessor startRouteProcessor = new StartRouteProcessor("pulSubmitCollectionFTPCgdProtectedRoute");
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        startRouteProcessor.process(ex);
    }
}
