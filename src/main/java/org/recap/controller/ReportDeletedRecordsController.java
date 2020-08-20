package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.service.deletedrecords.DeletedRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for processing Deleted Records Report
 *
 * Created by sudhish on 02/Jun/2017.
 */
@RestController
@RequestMapping("/reportDeleted")
public class ReportDeletedRecordsController {

    @Autowired
    private DeletedRecordsService deletedRecordsService;

    public DeletedRecordsService getDeletedRecordsService() {
        return deletedRecordsService;
    }

    /**
     * This method processes, the deleted records, by sending email notification and then updating record status
     *
     * @return
     */
    @GetMapping(value = "/records")
    public ResponseEntity deletedRecords() {
        String responseMsg = (getDeletedRecordsService().deletedRecords())? RecapConstants.DELETED_RECORDS_SUCCESS_MSG : RecapConstants.DELETED_RECORDS_FAILURE_MSG;
        return new ResponseEntity(responseMsg, HttpStatus.OK);
    }
}
