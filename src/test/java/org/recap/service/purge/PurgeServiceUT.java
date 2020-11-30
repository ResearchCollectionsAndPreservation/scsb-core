package org.recap.service.purge;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.repository.jpa.AccessionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 13/4/17.
 */
public class PurgeServiceUT extends BaseTestCaseUT {

    @InjectMocks
    PurgeService purgeService;

    @Mock
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    AccessionDetailsRepository accessionDetailsRepository;

    @Test
    public void testPurgeEmailAddress() {
        String[] responses={RecapConstants.EDD_REQUEST,""};
        for (String response:responses) {
            List<RequestTypeEntity> requestTypeEntityList = new ArrayList<>();
            RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
            requestTypeEntity.setRequestTypeCode(response);
            requestTypeEntityList.add(requestTypeEntity);
            Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntityList);
            Mockito.when(requestItemDetailsRepository.purgeEmailId(Mockito.anyList(), Mockito.any(), Mockito.anyInt(), Mockito.anyString())).thenReturn(1);
            Map<String, String> responseMap = purgeService.purgeEmailAddress();
            assertNotNull(responseMap);
            assertEquals(RecapCommonConstants.SUCCESS, responseMap.get(RecapCommonConstants.STATUS));
            assertNotNull(responseMap.get(RecapCommonConstants.PURGE_EDD_REQUEST));
            assertNotNull(responseMap.get(RecapCommonConstants.PURGE_PHYSICAL_REQUEST));
        }
        }

    @Test
    public void testPurgeEmailAddressException() {
        List<RequestTypeEntity> requestTypeEntityList =new ArrayList<>();
        RequestTypeEntity requestTypeEntity=new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("");
        requestTypeEntityList.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        Map<String, String> responseMap = purgeService.purgeEmailAddress();
        assertNotNull(responseMap);
        assertEquals(RecapCommonConstants.FAILURE,responseMap.get(RecapCommonConstants.STATUS));
    }

    @Test
    public void testPurgeExceptionRequests() {
        Map<String, String> responseMap = purgeService.purgeExceptionRequests();
        assertNotNull(responseMap);
        assertEquals(RecapCommonConstants.SUCCESS, responseMap.get(RecapCommonConstants.STATUS));
    }

    @Test
    public void testPurgeExceptionRequestsException() {
        ReflectionTestUtils.setField(purgeService,"requestItemDetailsRepository",null);
        Map<String, String> responseMap = purgeService.purgeExceptionRequests();
        assertNotNull(responseMap);
        assertEquals(RecapCommonConstants.FAILURE, responseMap.get(RecapCommonConstants.STATUS));
    }

    @Test
    public void testPurgeAccessionRequests() {
        Map<String, String> responseMap = purgeService.purgeAccessionRequests();
        assertNotNull(responseMap);
        assertEquals(RecapCommonConstants.SUCCESS, responseMap.get(RecapCommonConstants.STATUS));
    }

    @Test
    public void testPurgeAccessionRequestsException() {
        ReflectionTestUtils.setField(purgeService,"accessionDetailsRepository",null);
        Map<String, String> responseMap = purgeService.purgeAccessionRequests();
        assertNotNull(responseMap);
        assertEquals(RecapCommonConstants.FAILURE, responseMap.get(RecapCommonConstants.STATUS));
    }

}