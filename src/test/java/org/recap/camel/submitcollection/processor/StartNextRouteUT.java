package org.recap.camel.submitcollection.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.file.remote.SftpConfiguration;
import org.apache.camel.component.file.remote.SftpEndpoint;
import org.apache.camel.spi.RouteController;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;


public class StartNextRouteUT extends BaseTestCaseUT {

    @InjectMocks
    StartNextRoute mockedStartNextRoute;

    @Mock
    private ProducerTemplate producer;

    @Mock
    CamelContext camelContext;

    @Mock
    Exchange exchange;

    @Mock
    SftpEndpoint SftpEndpoint;

    @Mock
    SftpConfiguration SftpConfiguration;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    RouteController routeController;

    @Test
    public void process() throws Exception {
        String[] routeids={RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_PUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_PUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_CUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_CUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_NYPL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_NYPL_ROUTE};
        for (String routeid:routeids){
            ReflectionTestUtils.setField(mockedStartNextRoute, "routeId", routeid);
            Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
            mockedStartNextRoute.process(exchange);
            assertNotNull(routeid);
        }
    }
    @Test
    public void sendEmailForEmptyDirectory() throws Exception {
        String[] routeids={RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_PUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_PUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_CUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_CUL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_NYPL_ROUTE,RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_NYPL_ROUTE};
        for (String routeid:routeids){
            Mockito.when(exchange.getFromEndpoint()).thenReturn(SftpEndpoint);
            Mockito.when(SftpEndpoint.getConfiguration()).thenReturn(SftpConfiguration);
            Mockito.when(SftpConfiguration.getDirectoryName()).thenReturn("test");
            ReflectionTestUtils.setField(mockedStartNextRoute, "routeId", routeid);
            Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(Mockito.anyString(), Mockito.anyString())).thenReturn("test");
            mockedStartNextRoute.sendEmailForEmptyDirectory(exchange);
            assertNotNull(routeid);
        }
    }
    }
