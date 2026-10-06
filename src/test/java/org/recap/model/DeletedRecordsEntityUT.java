package org.recap.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.DeletedRecordsEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 13/7/17.
 */

public class DeletedRecordsEntityUT extends BaseTestCaseUT {

    @Test
    public void testDeletedRecordsEntity() {
        DeletedRecordsEntity deletedRecordsEntity = new DeletedRecordsEntity();
        deletedRecordsEntity.setId(1);
        deletedRecordsEntity.setRecords_Table("Test");
        deletedRecordsEntity.setRecordsPrimaryKey("Test");
        deletedRecordsEntity.setDeletedReportedStatus("Deleted");
        deletedRecordsEntity.setDeletedBy("Guest");
        deletedRecordsEntity.setDeletedDate(new Date());
        deletedRecordsEntity.setRecordsLog("Test");

        assertNotNull(deletedRecordsEntity.getDeletedBy());
        assertNotNull(deletedRecordsEntity.getDeletedDate());
        assertNotNull(deletedRecordsEntity.getId());
        assertNotNull(deletedRecordsEntity.getDeletedReportedStatus());
        assertNotNull(deletedRecordsEntity.getRecords_Table());
        assertNotNull(deletedRecordsEntity.getRecordsLog());
        assertNotNull(deletedRecordsEntity.getRecordsPrimaryKey());
    }

}