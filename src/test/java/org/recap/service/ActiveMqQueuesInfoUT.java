package org.recap.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

public class ActiveMqQueuesInfoUT extends BaseTestCaseUT {

    @InjectMocks
    ActiveMqQueuesInfo activeMqQueuesInfo;

    @Value("${activemq.jolokia.api.url}")
    private String activeMqApiUrl;

    @Value("${activemq.jolokia.api.queue.size.attribute}")
    private String searchAttribute;

    @Value("${activemq.web.console.url}")
    private String serviceUrl;

    @Value("${activemq.credentials}")
    private String activemqCredentials;

    @Test
    public void getActivemqQueuesInfo(){
        ReflectionTestUtils.setField(activeMqQueuesInfo,"serviceUrl",serviceUrl);
        ReflectionTestUtils.setField(activeMqQueuesInfo,"activemqCredentials",activemqCredentials);
        ReflectionTestUtils.setField(activeMqQueuesInfo,"activeMqApiUrl",activeMqApiUrl);
        ReflectionTestUtils.setField(activeMqQueuesInfo,"searchAttribute",searchAttribute);
        int queueSizeCount= activeMqQueuesInfo.getActivemqQueuesInfo("test");
        assertEquals(0,queueSizeCount);
    }
}
