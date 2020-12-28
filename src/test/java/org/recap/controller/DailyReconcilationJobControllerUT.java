package org.recap.controller;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 13/7/17.
 */
public class DailyReconcilationJobControllerUT extends BaseTestCase{

    @Autowired
    DailyReconcilationJobController dailyReconcilationJobController;

    @Test
    public void testDailyReconcilationJobController() throws Exception {
        String response = dailyReconcilationJobController.statCamel();
        assertNotNull(response);
        assertEquals("Success", response);
    }

}
