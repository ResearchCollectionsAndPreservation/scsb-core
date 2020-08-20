package org.recap.activemq;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;

import static org.junit.Assert.assertNull;


public class JmxHelperUT{
    private static final Logger logger = LoggerFactory.getLogger(JmxHelper.class);

    @Test
    public void testGetBeanForQueueName() {

        JmxHelper JmxHelper = new JmxHelper();
        DestinationViewMBean DestinationViewMBean = null;
        try {
            DestinationViewMBean = JmxHelper.getBeanForQueueName("test");
        } catch (Exception exception) {
            logger.info("Exception" + exception);
        }
        assertNull(DestinationViewMBean);
    }

    @Test
    public void testGetConnection() {
        JmxHelper JmxHelper = new JmxHelper();
        MBeanServerConnection MBeanServerConnection = null;
        try {
            MBeanServerConnection = JmxHelper.getConnection();
        } catch (Exception e) {
            logger.info("Exception" + e);
        }
        assertNull(MBeanServerConnection);
    }
}
