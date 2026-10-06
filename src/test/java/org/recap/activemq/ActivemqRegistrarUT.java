package org.recap.activemq;

import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;

import jakarta.jms.JMSException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ActivemqRegistrarUT extends BaseTestCaseUT {


    @Mock
    CamelContext camelContext;

    String defaultBrokerURL = "test";

    @Test
    public void getActivemqRegistrar() throws JMSException {
        ActivemqRegistrar activemqRegistrar = new ActivemqRegistrar(camelContext, defaultBrokerURL);
        assertNotNull(activemqRegistrar);
    }
}
