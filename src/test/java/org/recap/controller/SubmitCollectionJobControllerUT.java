package org.recap.controller;

import org.apache.camel.*;
import org.apache.camel.spi.RouteController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionJobControllerUT {

    @InjectMocks
    SubmitCollectionJobController submitCollectionJobController;

    @Mock
    private ProducerTemplate producer;

    @Mock
    private CamelContext camelContext;

    @Mock
    Endpoint endpoint;

    @Mock
    PollingConsumer pollingConsumer;

    @Mock
    Exchange exchange;

    @Mock
    RouteController routeController;

    @Mock
    Message message;

    @Test
    public void startSubmitCollection() throws Exception{
        message.setMessageId("1");
        message.setBody("SUBMIT COLLECTION");
        exchange.setIn(message);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.doNothing().when(routeController).startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_PUL_ROUTE);
        Mockito.when(camelContext.getEndpoint(RecapConstants.SUBMIT_COLLECTION_COMPLETION_QUEUE_TO)).thenReturn(endpoint);
        Mockito.when(endpoint.createPollingConsumer()).thenReturn(pollingConsumer);
        Mockito.when(pollingConsumer.receive()).thenReturn(exchange);
        Mockito.when(exchange.getIn()).thenReturn(message);
        String result = submitCollectionJobController.startSubmitCollection();
        assertNotNull(result);
    }
}
