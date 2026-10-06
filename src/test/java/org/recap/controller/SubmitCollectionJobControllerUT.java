package org.recap.controller;


import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.PollingConsumer;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.submitcollection.SubmitCollectionPollingS3RouteBuilder;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubmitCollectionJobControllerUT extends BaseTestCaseUT {

    @InjectMocks
    SubmitCollectionJobController submitCollectionJobController;

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

    @Mock
    CommonUtil commonUtil;

    @Mock
    SubmitCollectionPollingS3RouteBuilder submitCollectionPollingFtpRouteBuilder;

    @Test
    public void startSubmitCollection() throws Exception {
        message.setMessageId("1");
        message.setBody("SUBMIT COLLECTION");
        exchange.setIn(message);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.doNothing().when(routeController).startRoute("PUL" + ScsbConstants.CGD_PROTECTED_ROUTE_ID);
        Mockito.when(camelContext.getEndpoint(ScsbConstants.SUBMIT_COLLECTION_COMPLETION_QUEUE_TO)).thenReturn(endpoint);
        Mockito.when(endpoint.createPollingConsumer()).thenReturn(pollingConsumer);
        Mockito.when(pollingConsumer.receive()).thenReturn(exchange);
        Mockito.when(exchange.getIn()).thenReturn(message);
        List<String> allInstitutionCodeExceptSupportInstitution = Arrays.asList("PUL", "CUL", "NYPL");
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(allInstitutionCodeExceptSupportInstitution);
        String result = submitCollectionJobController.startSubmitCollection();
        assertEquals(ScsbCommonConstants.SUCCESS, result);
    }

    @Test
    public void startSubmitCollectionException() throws Exception {
        message.setMessageId("1");
        message.setBody("SUBMIT COLLECTION");
        exchange.setIn(message);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.doNothing().when(routeController).startRoute("PUL" + ScsbConstants.CGD_PROTECTED_ROUTE_ID);
        Mockito.when(camelContext.getEndpoint(ScsbConstants.SUBMIT_COLLECTION_COMPLETION_QUEUE_TO)).thenReturn(endpoint);
        Mockito.when(endpoint.createPollingConsumer()).thenThrow(NullPointerException.class);
        Mockito.when(pollingConsumer.receive()).thenReturn(exchange);
        Mockito.when(exchange.getIn()).thenReturn(message);
        List<String> allInstitutionCodeExceptSupportInstitution = Arrays.asList("PUL", "CUL", "NYPL");
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(allInstitutionCodeExceptSupportInstitution);
        String result = submitCollectionJobController.startSubmitCollection();
        assertEquals(ScsbCommonConstants.SUCCESS, result);
    }
}
