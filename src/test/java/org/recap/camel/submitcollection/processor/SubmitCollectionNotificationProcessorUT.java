package org.recap.camel.submitcollection.processor;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionNotificationProcessorUT {
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
