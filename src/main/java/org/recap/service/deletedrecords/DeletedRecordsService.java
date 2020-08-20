package org.recap.service.deletedrecords;

import org.recap.RecapConstants;
import org.recap.repository.jpa.DeletedRecordsRepository;
import org.recap.request.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sudhishk on 2/6/17.
 */
@Service
public class DeletedRecordsService {

    private static final Logger logger = LoggerFactory.getLogger(DeletedRecordsService.class);

    @Autowired
    private DeletedRecordsRepository deletedRecordsRepository;

    @Autowired
    private EmailService emailService;

    /**
     * @return boolean
     */
    public boolean deletedRecords() {
        boolean bReturnMsg = false;

        try {
            long lCountDeleted = deletedRecordsRepository.countByDeletedReportedStatus(RecapConstants.DELETED_STATUS_NOT_REPORTED);
            logger.info("Count : {}", lCountDeleted);
            if (lCountDeleted > 0) {
                // Change Status
                int statusChange = deletedRecordsRepository.updateDeletedReportedStatus(RecapConstants.DELETED_STATUS_REPORTED, RecapConstants.DELETED_STATUS_NOT_REPORTED);
                logger.info("Delete Count : {}" , statusChange);
                // Send Email
                emailService.sendEmail(RecapConstants.EMAIL_DELETED_RECORDS_DISPLAY_MESSAGE + lCountDeleted, "", RecapConstants.DELETED_MAIl_TO, RecapConstants.EMAIL_SUBJECT_DELETED_RECORDS);
            } else {
                logger.info("No records to delete" );
            }
            bReturnMsg = true;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return bReturnMsg;
    }
}
