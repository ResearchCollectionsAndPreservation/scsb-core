package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.repository.jpa.InstitutionDetailsRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
public class AccessionReconcilationJobControllerUT extends BaseTestCaseUT {

    @InjectMocks
    AccessionReconcilationJobController accessionReconcilationJobController;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    CamelContext camelContext;

    @Mock
    RouteController routeController;

    @Test
    public void startAccessionReconcilation() throws Exception{
        List<String> allInstitutionCodeExceptHTC=new ArrayList<>();
        allInstitutionCodeExceptHTC.add("PUL");
        allInstitutionCodeExceptHTC.add("CUL");
        allInstitutionCodeExceptHTC.add("NYPL");
        allInstitutionCodeExceptHTC.add("HUL");
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(allInstitutionCodeExceptHTC);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.doNothing().when(routeController).startRoute(Mockito.anyString());
        String result = accessionReconcilationJobController.startAccessionReconcilation();
        assertNotNull(result);
        assertEquals(RecapCommonConstants.SUCCESS,result);
    }
}
