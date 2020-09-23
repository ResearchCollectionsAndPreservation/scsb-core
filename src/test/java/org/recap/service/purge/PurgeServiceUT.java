package org.recap.service.purge;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapCommonConstants;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 13/4/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurgeServiceUT{

    @InjectMocks
    PurgeService purgeService;

    @Test
    public void testPurgeEmailAddress() {
        Map<String, String> responseMap = purgeService.purgeEmailAddress();
        assertNotNull(responseMap);
        assertNotNull(responseMap.get(RecapCommonConstants.STATUS));
    }
    @Test
    public void testPurgeEmailAddressForException() {
        PurgeService purgeService = new PurgeService();
        Map<String, String> responseMap = purgeService.purgeEmailAddress();
        assertNotNull(responseMap);
        assertNotNull(responseMap.get(RecapCommonConstants.STATUS));
    }

    @Test
    public void testPurgeExceptionRequests() {
        Map<String, String> responseMap = purgeService.purgeExceptionRequests();
        assertNotNull(responseMap);
        assertNotNull(responseMap.get(RecapCommonConstants.STATUS));
    }
    @Test
    public void testPurgeExceptionRequestsForException() {
        PurgeService purgeService = new PurgeService();
        Map<String, String> responseMap = purgeService.purgeExceptionRequests();
        assertNotNull(responseMap);
        assertNotNull(responseMap.get(RecapCommonConstants.STATUS));
    }

    @Test
    public void testPurgeAccessionRequests() {
        Map<String, String> responseMap = purgeService.purgeAccessionRequests();
        assertNotNull(responseMap);
        assertNotNull(responseMap.get(RecapCommonConstants.STATUS));
    }
    @Test
    public void testPurgeAccessionRequestsForException() {
        PurgeService purgeService = new PurgeService();
        Map<String, String> responseMap = purgeService.purgeAccessionRequests();
        assertNotNull(responseMap);
        assertNotNull(responseMap.get(RecapCommonConstants.STATUS));
    }

}