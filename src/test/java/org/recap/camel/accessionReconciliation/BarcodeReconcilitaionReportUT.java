package org.recap.camel.accessionReconciliation;

import org.junit.Test;
import org.recap.camel.accessionreconciliation.BarcodeReconcilitaionReport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BarcodeReconcilitaionReportUT {

    @Test
    public void getBarcodeReconcilitaionReport(){
        BarcodeReconcilitaionReport barcodeReconcilitaionReport = new BarcodeReconcilitaionReport();
        barcodeReconcilitaionReport.setStatus("Complete");
        barcodeReconcilitaionReport.setCustomerCode("PA");
        barcodeReconcilitaionReport.setBarcode("12354");
        assertNotNull(barcodeReconcilitaionReport.getBarcode());
        assertNotNull(barcodeReconcilitaionReport.getCustomerCode());
        assertNotNull(barcodeReconcilitaionReport.getStatus());
    }

}
