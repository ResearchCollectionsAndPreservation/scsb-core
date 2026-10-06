package org.recap.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActiveMqQueuesInfoUT extends BaseTestCaseUT {

    @InjectMocks
    private ActiveMqQueuesInfo activeMqQueuesInfo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(activeMqQueuesInfo, "serviceUrl", "http://localhost:8161/admin");
        ReflectionTestUtils.setField(activeMqQueuesInfo, "activemqCredentials", "admin:admin");
        ReflectionTestUtils.setField(activeMqQueuesInfo, "activeMqApiUrl", "http://localhost:8161/api/jolokia");
        ReflectionTestUtils.setField(activeMqQueuesInfo, "searchAttribute", "QueueSize");
    }

    @Test
    public void getActivemqQueuesInfo() {
        int queueSizeCount = activeMqQueuesInfo.getActivemqQueuesInfo("test");
        assertEquals(0, queueSizeCount);
    }
}
