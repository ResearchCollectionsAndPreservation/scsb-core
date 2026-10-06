package org.recap.activemq;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.management.MBeanServerConnection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class JmxHelperUT {

    @InjectMocks
    JmxHelper jmxHelper;

    String serviceUrl = "https://serviceUrl.com";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(jmxHelper, "serviceUrl", serviceUrl);
    }

    @Test
    public void testGetBeanForQueueName() {
        MBeanServerConnection connection = Mockito.mock(MBeanServerConnection.class);
        ReflectionTestUtils.setField(jmxHelper, "connection", connection);
        DestinationViewMBean DestinationViewMBean = null;
        DestinationViewMBean = jmxHelper.getBeanForQueueName("test");
        assertNotNull(DestinationViewMBean);
    }

    @Test
    public void testGetBeanForQueueNameNull() {
        MBeanServerConnection connection = Mockito.mock(MBeanServerConnection.class);
        ReflectionTestUtils.setField(jmxHelper, "connection", connection);
        DestinationViewMBean DestinationViewMBean = null;
        DestinationViewMBean = jmxHelper.getBeanForQueueName(null);
        assertNotNull(DestinationViewMBean);
    }

    @Test
    public void testGetConnection() {
        MBeanServerConnection connection = null;
        try {
            connection = jmxHelper.getConnection();
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        assertNull(connection);
    }

}
