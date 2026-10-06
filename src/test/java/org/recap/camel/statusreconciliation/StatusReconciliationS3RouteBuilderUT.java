package org.recap.camel.statusreconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.springframework.context.ApplicationContext;

public class StatusReconciliationS3RouteBuilderUT extends BaseTestCaseUT {

    @Mock
    ApplicationContext applicationContext;

    @Test
    public void call() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        StatusReconciliationS3RouteBuilder statusReconciliationS3RouteBuilder = new StatusReconciliationS3RouteBuilder(ctx, applicationContext, true, "status.reconciliation");
    }
}
