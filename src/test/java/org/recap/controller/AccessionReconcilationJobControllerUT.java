package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.repository.jpa.ImsLocationDetailsRepository;
import org.recap.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccessionReconcilationJobControllerUT extends BaseTestCaseUT {

    @InjectMocks
    AccessionReconcilationJobController accessionReconcilationJobController;

    @Mock
    CommonUtil commonUtil;

    @Mock
    CamelContext camelContext;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    RouteController routeController;

    @Test
    public void startAccessionReconcilation() throws Exception {
        List<String> allImsLocationCodeExceptUN = new ArrayList<>();
        allImsLocationCodeExceptUN.add("RECAP");
        allImsLocationCodeExceptUN.add("HD");
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCodeExceptUN()).thenReturn(allImsLocationCodeExceptUN);
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(getInstitutionCodeExceptSupportInstitution());
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.doNothing().when(routeController).startRoute(Mockito.anyString());
        String result = accessionReconcilationJobController.startAccessionReconciliation();
        assertNotNull(result);
        assertEquals(ScsbCommonConstants.SUCCESS, result);
    }

    private List<String> getInstitutionCodeExceptSupportInstitution() {
        List<String> allInstitutionCodeExceptSupportInstitution = new ArrayList<>();
        allInstitutionCodeExceptSupportInstitution.add("PUL");
        allInstitutionCodeExceptSupportInstitution.add("CUL");
        allInstitutionCodeExceptSupportInstitution.add("NYPL");
        allInstitutionCodeExceptSupportInstitution.add("HL");
        return allInstitutionCodeExceptSupportInstitution;
    }
}
