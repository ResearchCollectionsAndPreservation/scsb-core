package org.recap.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class ActiveMqQueuesInfoUT extends BaseTestCase {
    @Autowired
    ActiveMqQueuesInfo activeMqQueuesInfo;

    @Test
    public void testActiveMqQueuesInfo(){
        Integer value = activeMqQueuesInfo.getActivemqQueuesInfo("recap");
        String res = activeMqQueuesInfo.getEncodedActivemqCredentials();
        assertNotNull(res);
    }
}
