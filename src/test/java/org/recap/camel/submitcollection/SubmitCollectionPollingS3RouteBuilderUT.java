package org.recap.camel.submitcollection;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.RouteController;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.ILSConfigProperties;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.PropertyUtil;

import java.util.ArrayList;
import java.util.List;

public class SubmitCollectionPollingS3RouteBuilderUT extends BaseTestCaseUT {

    @InjectMocks
    SubmitCollectionPollingS3RouteBuilder submitCollectionPollingS3RouteBuilder;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    CamelContext camelContext;

    @Mock
    RouteController routeController;

    @Mock
    Exchange exchange;

    @Mock
    ProducerTemplate producer;

    @Test
    public void createRoutesForSubmitCollection() throws Exception {
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(getStrings());
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        submitCollectionPollingS3RouteBuilder.createRoutesForSubmitCollection();
    }

    @Test
    public void startNextRouteInNewThreadException() throws Exception {
        submitCollectionPollingS3RouteBuilder.startNextRouteInNewThread(exchange,"1");
    }

    @Test
    public void startNextRouteInNewThread() throws Exception {
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        submitCollectionPollingS3RouteBuilder.startNextRouteInNewThread(exchange,"1");
    }

    @Test
    public void startNextRouteInNewThreadComplete() throws Exception {
        submitCollectionPollingS3RouteBuilder.startNextRouteInNewThread(exchange,"1Complete");

    }

    @Test
    public void removeRoutesForSubmitCollection() throws Exception {
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(getStrings());
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        submitCollectionPollingS3RouteBuilder.removeRoutesForSubmitCollection();
    }


    private List<String> getStrings() {
        List<String> allInstitutionCodeExceptHTC=new ArrayList<>();
        allInstitutionCodeExceptHTC.add("PUL");
        allInstitutionCodeExceptHTC.add("CUL");
        allInstitutionCodeExceptHTC.add("NYPL");
        return allInstitutionCodeExceptHTC;
    }
}
