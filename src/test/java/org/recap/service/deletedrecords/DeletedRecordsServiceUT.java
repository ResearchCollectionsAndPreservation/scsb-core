package org.recap.service.deletedrecords;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.repository.jpa.DeletedRecordsRepository;
import org.recap.service.EmailService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by sudhishk on 5/6/17.
 */

public class DeletedRecordsServiceUT extends BaseTestCaseUT {
    @InjectMocks
    DeletedRecordsService deletedRecordsService;

    @Mock
    private DeletedRecordsRepository deletedRecordsRepository;

    @Mock
    private EmailService emailService;

    @Test
    public void testdeletedRecords() {
        Long lCountDeleted = 12345678910L;
        Mockito.when(deletedRecordsRepository.countByDeletedReportedStatus(ScsbConstants.DELETED_STATUS_NOT_REPORTED)).thenReturn(lCountDeleted);
        Mockito.when(deletedRecordsRepository.updateDeletedReportedStatus(ScsbConstants.DELETED_STATUS_REPORTED, ScsbConstants.DELETED_STATUS_NOT_REPORTED)).thenReturn(1);
        Mockito.doNothing().when(emailService).sendEmail(ScsbConstants.EMAIL_DELETED_RECORDS_DISPLAY_MESSAGE + lCountDeleted, "", ScsbConstants.DELETED_MAIL_TO, ScsbConstants.EMAIL_SUBJECT_DELETED_RECORDS);
        boolean bflag = deletedRecordsService.deletedRecords();
        assertTrue(bflag);
    }

    @Test
    public void testdeletedRecordsEmpty() {
        Long lCountDeleted = 0l;
        Mockito.when(deletedRecordsRepository.countByDeletedReportedStatus(ScsbConstants.DELETED_STATUS_NOT_REPORTED)).thenReturn(lCountDeleted);
        Mockito.when(deletedRecordsRepository.updateDeletedReportedStatus(ScsbConstants.DELETED_STATUS_REPORTED, ScsbConstants.DELETED_STATUS_NOT_REPORTED)).thenReturn(1);
        Mockito.doNothing().when(emailService).sendEmail(ScsbConstants.EMAIL_DELETED_RECORDS_DISPLAY_MESSAGE + lCountDeleted, "", ScsbConstants.DELETED_MAIL_TO, ScsbConstants.EMAIL_SUBJECT_DELETED_RECORDS);
        boolean bflag = deletedRecordsService.deletedRecords();
        assertTrue(bflag);
    }

    @Test
    public void testdeletedRecordsException() {
        Long lCountDeleted = 0l;
        Mockito.when(deletedRecordsRepository.countByDeletedReportedStatus(ScsbConstants.DELETED_STATUS_NOT_REPORTED)).thenThrow(NullPointerException.class);
        Mockito.when(deletedRecordsRepository.updateDeletedReportedStatus(ScsbConstants.DELETED_STATUS_REPORTED, ScsbConstants.DELETED_STATUS_NOT_REPORTED)).thenReturn(1);
        Mockito.doNothing().when(emailService).sendEmail(ScsbConstants.EMAIL_DELETED_RECORDS_DISPLAY_MESSAGE + lCountDeleted, "", ScsbConstants.DELETED_MAIL_TO, ScsbConstants.EMAIL_SUBJECT_DELETED_RECORDS);
        boolean bflag = deletedRecordsService.deletedRecords();
        assertFalse(bflag);
    }
}
