package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.service.purge.PurgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 12/4/17.
 */
public class PurgeControllerUT extends BaseTestCaseUT {

    @InjectMocks
    PurgeController purgeEmailAddressController;

    @Mock
    PurgeService purgeService;

    @Test
    public void testPurgeEmailAddress() {
        ResponseEntity responseEntity = purgeEmailAddressController.purgeEmailAddress();
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testPurgeExceptionRequests() {
        ResponseEntity responseEntity = purgeEmailAddressController.purgeExceptionRequests();
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testPurgeAccessionRequests() {
        ResponseEntity responseEntity = purgeEmailAddressController.purgeAccessionRequests();
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
    }

}