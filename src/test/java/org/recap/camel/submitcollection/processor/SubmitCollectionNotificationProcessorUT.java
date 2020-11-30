package org.recap.camel.submitcollection.processor;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertTrue;

public class SubmitCollectionNotificationProcessorUT extends BaseTestCaseUT {
    @InjectMocks
    SubmitCollectionNotificationProcessor submitCollectionNotificationProcessor;
    @Mock
    private ProducerTemplate producer;

    @Test
    public void testSubmitCollectionNotificationProcessor(){
        submitCollectionNotificationProcessor.sendSubmitCollectionNotification();
        assertTrue(true);
    }
}
