package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 13/7/17.
 */
public class SubmitCollectionReportRecordUT extends BaseTestCaseUT {

    @Test
    public void testSubmitCollectionReportRecord() {
        SubmitCollectionReportRecord submitCollectionReportRecord = new SubmitCollectionReportRecord();
        submitCollectionReportRecord.setOwningInstitution("PUL");
        submitCollectionReportRecord.setCustomerCode("PB");
        submitCollectionReportRecord.setItemBarcode("33256845687546764");
        submitCollectionReportRecord.setMessage("Success");

        assertNotNull(submitCollectionReportRecord.getMessage());
        assertNotNull(submitCollectionReportRecord.getCustomerCode());
        assertNotNull(submitCollectionReportRecord.getItemBarcode());
        assertNotNull(submitCollectionReportRecord.getOwningInstitution());
    }

}
