package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.service.deletedrecords.DeletedRecordsService;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 13/7/17.
 */
public class ReportDeletedRecordsControllerUT extends BaseTestCase{

    @Mock
    ReportDeletedRecordsController reportDeletedRecordsController;

    @Mock
    DeletedRecordsService deletedRecordsService;

    @Test
    public void testReportDeletedRecordsController(){
        Mockito.when(reportDeletedRecordsController.getDeletedRecordsService()).thenReturn(deletedRecordsService);
        Mockito.when(deletedRecordsService.deletedRecords()).thenReturn(true);
        Mockito.when(reportDeletedRecordsController.deletedRecords()).thenCallRealMethod();
        ResponseEntity responseEntity = reportDeletedRecordsController.deletedRecords();
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(), RecapConstants.DELETED_RECORDS_SUCCESS_MSG);
        Mockito.when(reportDeletedRecordsController.getDeletedRecordsService()).thenCallRealMethod();
        assertNotEquals(reportDeletedRecordsController.getDeletedRecordsService(),deletedRecordsService);
    }

}