package org.recap.model.csv;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 13/7/17.
 */
public class DailyReconcilationRecordUT extends BaseTestCaseUT {

    @Test
    public void testDailyReconciliationRecord() {

        DailyReconcilationRecord dailyReconcilationRecord = new DailyReconcilationRecord();
        dailyReconcilationRecord.setRequestId("1");
        dailyReconcilationRecord.setBarcode("33245555767876");
        dailyReconcilationRecord.setCustomerCode("AD");
        dailyReconcilationRecord.setStopCode("AD");
        dailyReconcilationRecord.setPatronId("000000");
        dailyReconcilationRecord.setCreateDate(new Date().toString());
        dailyReconcilationRecord.setLastUpdatedDate(new Date().toString());
        dailyReconcilationRecord.setRequestingInst("PUL");
        dailyReconcilationRecord.setOwningInst("CUL");
        dailyReconcilationRecord.setDeliveryMethod("Test");
        dailyReconcilationRecord.setStatus("Available");
        dailyReconcilationRecord.setEmail("test@email.com");
        dailyReconcilationRecord.setErrorCode("Test");
        dailyReconcilationRecord.setErrorNote("Test");

        assertNotNull(dailyReconcilationRecord.getRequestId());
        assertNotNull(dailyReconcilationRecord.getBarcode());
        assertNotNull(dailyReconcilationRecord.getCustomerCode());
        assertNotNull(dailyReconcilationRecord.getStopCode());
        assertNotNull(dailyReconcilationRecord.getPatronId());
        assertNotNull(dailyReconcilationRecord.getCreateDate());
        assertNotNull(dailyReconcilationRecord.getLastUpdatedDate());
        assertNotNull(dailyReconcilationRecord.getRequestingInst());
        assertNotNull(dailyReconcilationRecord.getOwningInst());
        assertNotNull(dailyReconcilationRecord.getDeliveryMethod());
        assertNotNull(dailyReconcilationRecord.getStatus());
        assertNotNull(dailyReconcilationRecord.getEmail());
        assertNotNull(dailyReconcilationRecord.getErrorCode());
        assertNotNull(dailyReconcilationRecord.getErrorNote());

    }

}
