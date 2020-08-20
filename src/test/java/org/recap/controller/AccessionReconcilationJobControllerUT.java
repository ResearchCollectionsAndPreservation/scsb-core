package org.recap.controller;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
public class AccessionReconcilationJobControllerUT extends BaseTestCase {

    @Autowired
    AccessionReconcilationJobController accessionReconcilationJobController;
    @Test
    public void startAccessionReconcilation() throws Exception{
        String result = accessionReconcilationJobController.startAccessionReconcilation();
        assertNotNull(result);
        assertEquals("Success",result);
    }
}
