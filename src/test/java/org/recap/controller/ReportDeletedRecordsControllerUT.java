package org.recap.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapConstants;
import org.recap.service.deletedrecords.DeletedRecordsService;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ReportDeletedRecordsControllerUT{

    @InjectMocks
    ReportDeletedRecordsController reportDeletedRecordsController;

    @Mock
    DeletedRecordsService deletedRecordsService;

    @Test
    public void testReportDeletedRecordsController(){
        Mockito.when(deletedRecordsService.deletedRecords()).thenReturn(true);
        ResponseEntity responseEntity = reportDeletedRecordsController.deletedRecords();
        assertNotNull(responseEntity);
        assertEquals(RecapConstants.DELETED_RECORDS_SUCCESS_MSG,responseEntity.getBody());
    }

}