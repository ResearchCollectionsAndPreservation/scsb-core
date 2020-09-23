package org.recap.model.csv;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AccessionSummaryRecordUT {

    @Test
    public void getAccessionSummaryRecord(){
        AccessionSummaryRecord accessionSummaryRecord = new AccessionSummaryRecord();
        accessionSummaryRecord.setFailedBibCount("1");
        accessionSummaryRecord.setFailedItemCount("1");
        accessionSummaryRecord.setNoOfBibMatches("20");
        accessionSummaryRecord.setReasonForFailureBib("Test");
        accessionSummaryRecord.setReasonForFailureItem("test");
        accessionSummaryRecord.setSuccessBibCount("2");
        accessionSummaryRecord.setSuccessItemCount("2");
        assertNotNull(accessionSummaryRecord.getFailedBibCount());
        assertNotNull(accessionSummaryRecord.getFailedItemCount());
        assertNotNull(accessionSummaryRecord.getNoOfBibMatches());
        assertNotNull(accessionSummaryRecord.getReasonForFailureBib());
        assertNotNull(accessionSummaryRecord.getReasonForFailureItem());
        assertNotNull(accessionSummaryRecord.getSuccessBibCount());
        assertNotNull(accessionSummaryRecord.getSuccessItemCount());
    }
}
