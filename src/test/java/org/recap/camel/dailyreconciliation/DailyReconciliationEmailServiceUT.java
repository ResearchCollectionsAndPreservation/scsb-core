package org.recap.camel.dailyreconciliation;


import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.Silent.class)
public class DailyReconciliationEmailServiceUT  {

    @Mock
    private Exchange exchange;

    @Mock
    private ProducerTemplate producerTemplate;

    @Mock
    private PropertyUtil propertyUtil;

    @InjectMocks
    private DailyReconciliationEmailService dailyReconciliationEmailService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        try {
            dailyReconciliationEmailService.process(exchange);
            verify(producerTemplate, times(1)).sendBodyAndHeader(eq(ScsbConstants.EMAIL_Q), any(), eq(ScsbConstants.EMAIL_BODY_FOR), eq(ScsbConstants.DAILY_RECONCILIATION));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetEmailPayLoad() {
        ReflectionTestUtils.invokeMethod(dailyReconciliationEmailService, "getEmailPayLoad");
    }
}
