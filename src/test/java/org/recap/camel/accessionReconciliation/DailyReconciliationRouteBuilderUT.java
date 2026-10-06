package org.recap.camel.accessionReconciliation;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.camel.dailyreconciliation.DailyReconciliationRouteBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

public class DailyReconciliationRouteBuilderUT extends BaseTestCaseUT {

    @InjectMocks
    DailyReconciliationRouteBuilder dailyReconciliationRouteBuilder;

    @Mock
    private ApplicationContext applicationContext;

    private String imsLocation;
    String dailyReconciliationS3;
    String dailyReconciliationFtpProcessed;
    String dailyReconciliationFilePath;

    @Test
    public void setup() {
        ReflectionTestUtils.setField(dailyReconciliationRouteBuilder, "imsLocation", imsLocation);
        ReflectionTestUtils.setField(dailyReconciliationRouteBuilder, "dailyReconciliationS3", dailyReconciliationS3);
        ReflectionTestUtils.setField(dailyReconciliationRouteBuilder, "dailyReconciliationFtpProcessed", dailyReconciliationFtpProcessed);
        ReflectionTestUtils.setField(dailyReconciliationRouteBuilder, "dailyReconciliationFilePath", dailyReconciliationFilePath);
    }

    @Test
    public void configure() throws Exception {
        dailyReconciliationRouteBuilder.configure();
    }
}
