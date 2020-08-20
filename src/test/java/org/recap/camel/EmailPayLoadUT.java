package org.recap.camel;

import org.junit.Test;
import org.recap.BaseTestCase;

public class EmailPayLoadUT extends BaseTestCase {
    @Test
    public  void testEmailPayLoad(){
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.getItemBarcode();
        emailPayLoad.getPatronBarcode();
        emailPayLoad.getCustomerCode();
        emailPayLoad.getMessageDisplay();
        emailPayLoad.getSubject();
        emailPayLoad.getLocation();
        emailPayLoad.getInstitution();
        emailPayLoad.getReportFileName();
        emailPayLoad.setReportFileName("test");
        emailPayLoad.getReportFileName();
        emailPayLoad.getXmlFileName();
        emailPayLoad.getException();
        emailPayLoad.getExceptionMessage();
        emailPayLoad.getPendingRequestLimit();
        emailPayLoad.setPendingRequestLimit("test");
        emailPayLoad.setBulkRequestId("test");
        emailPayLoad.getBulkRequestId();
        emailPayLoad.getBulkRequestName();
        emailPayLoad.setBulkRequestName("test");
        emailPayLoad.getBulkRequestFileName();
        emailPayLoad.setBulkRequestFileName("test");
        emailPayLoad.getBulkRequestStatus();
        emailPayLoad.setBulkRequestStatus("test");
        emailPayLoad.getBulkRequestCsvFileData();
        emailPayLoad.setBulkRequestCsvFileData("test");
    }
}
