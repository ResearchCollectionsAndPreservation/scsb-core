package org.recap.camel.accessionReconciliation;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.camel.accessionreconciliation.BarcodeReconciliationRouteBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

public class BarcodeReconciliationRouteBuilderUT extends BaseTestCaseUT {

    @InjectMocks
    BarcodeReconciliationRouteBuilder barcodeReconciliationRouteBuilder;

    @Mock
    ApplicationContext applicationContext;

    String institution = "PUL";
    String imsLocation = "HD";
    String accessionReconciliationS3Dir = "test";
    String accessionReconciliationFilePath = "test";
    String s3AccessionReconciliationProcessedDir = "test";

    @Test
    public void setup() {
        ReflectionTestUtils.setField(barcodeReconciliationRouteBuilder, "institution", institution);
        ReflectionTestUtils.setField(barcodeReconciliationRouteBuilder, "imsLocation", imsLocation);
        ReflectionTestUtils.setField(barcodeReconciliationRouteBuilder, "accessionReconciliationS3Dir", accessionReconciliationS3Dir);
        ReflectionTestUtils.setField(barcodeReconciliationRouteBuilder, "accessionReconciliationFilePath", accessionReconciliationFilePath);
        ReflectionTestUtils.setField(barcodeReconciliationRouteBuilder, "s3AccessionReconciliationProcessedDir", s3AccessionReconciliationProcessedDir);
    }

    @Test
    public void configure() throws Exception {
        barcodeReconciliationRouteBuilder.configure();
    }
}
