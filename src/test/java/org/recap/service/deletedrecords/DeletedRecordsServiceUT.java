package org.recap.service.deletedrecords;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.repository.jpa.DeletedRecordsRepository;
import org.recap.request.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

/**
 * Created by sudhishk on 5/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeletedRecordsServiceUT{
    @InjectMocks
    DeletedRecordsService deletedRecordsService;

    @Mock
    private DeletedRecordsRepository deletedRecordsRepository;

    @Mock
    private EmailService emailService;

    @Test
    public void testdeletedRecords(){
        Long lCountDeleted = 12345678910L;
        Mockito.when(deletedRecordsRepository.countByDeletedReportedStatus(RecapConstants.DELETED_STATUS_NOT_REPORTED)).thenReturn(lCountDeleted);
        Mockito.when(deletedRecordsRepository.updateDeletedReportedStatus(RecapConstants.DELETED_STATUS_REPORTED, RecapConstants.DELETED_STATUS_NOT_REPORTED)).thenReturn(1);
        Mockito.doNothing().when(emailService).sendEmail(RecapConstants.EMAIL_DELETED_RECORDS_DISPLAY_MESSAGE + lCountDeleted, "", RecapConstants.DELETED_MAIl_TO, RecapConstants.EMAIL_SUBJECT_DELETED_RECORDS);
        boolean bflag = deletedRecordsService.deletedRecords();
        assertTrue(bflag);
    }
}
