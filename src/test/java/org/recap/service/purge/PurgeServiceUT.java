package org.recap.service.purge;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 13/4/17.
 */
public class PurgeServiceUT extends BaseTestCase {

    @Autowired
    PurgeService purgeService;

    @Test
    public void testPurgeEmailAddress() {
        Map<String, String> responseMap = purgeService.purgeEmailAddress();
        assertNotNull(responseMap);
        assertNotNull(responseMap.get(RecapCommonConstants.STATUS));
        assertNotNull(responseMap.get(RecapCommonConstants.PURGE_EDD_REQUEST));
        assertNotNull(responseMap.get(RecapCommonConstants.PURGE_PHYSICAL_REQUEST));
    }

    @Test
    public void testPurgeExceptionRequests() {
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

}