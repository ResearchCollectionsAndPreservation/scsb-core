package org.recap.model.queueinfo;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;

public class QueueSizeInfoJsonUT extends BaseTestCase {
    @Test
    public void testQueueSizeInfoJson() {
        QueueSizeInfoJson queueSizeInfoJson = new QueueSizeInfoJson();
        queueSizeInfoJson.setValue("test");
        assertNotNull(queueSizeInfoJson.getValue());
    }
}
