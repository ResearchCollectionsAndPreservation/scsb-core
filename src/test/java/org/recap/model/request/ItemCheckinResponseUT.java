package org.recap.model.request;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ItemCheckinResponseUT {

    @Test
    public void getItemCheckinResponse(){
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        itemCheckinResponse.setAlert(true);
        itemCheckinResponse.setAlertType("Alert test");
        itemCheckinResponse.setBibId("12356");
        itemCheckinResponse.setCallNumber("24578");
        itemCheckinResponse.setCollectionCode("498758");
        itemCheckinResponse.setCreatedDate(new Date().toString());
        itemCheckinResponse.setCurrencyType("INR");
        itemCheckinResponse.setDestinationLocation("PA");
        itemCheckinResponse.setDueDate(new Date().toString());
        itemCheckinResponse.setEsipDataIn("IN");
        itemCheckinResponse.setEsipDataOut("OUT");
        itemCheckinResponse.setFeeAmount("2134");
        itemCheckinResponse.setFeeType("FINE");
        itemCheckinResponse.setHoldPatronId("43677");
        itemCheckinResponse.setHoldPatronName("hold");
        itemCheckinResponse.setInstitutionID("1");
        itemCheckinResponse.setISBN("isbn");
        itemCheckinResponse.setItemBarcode("132454");
        itemCheckinResponse.setItemBarcodes(Arrays.asList("24567"));
        itemCheckinResponse.setItemOwningInstitution("3");
        itemCheckinResponse.setJobId("1");
        itemCheckinResponse.setLCCN("lcccn");
        itemCheckinResponse.setMagneticMedia(true);
        itemCheckinResponse.setMediaType("media test");
        itemCheckinResponse.setPatronIdentifier("678009");
        itemCheckinResponse.setPermanentLocation("PA");
        itemCheckinResponse.setProcessed(true);
        itemCheckinResponse.setResensitize(true);
        itemCheckinResponse.setScreenMessage("success");
        itemCheckinResponse.setSecurityInhibit("test");
        itemCheckinResponse.setSortBin("test");
        itemCheckinResponse.setSuccess(true);
        itemCheckinResponse.setTitleIdentifier("title");
        itemCheckinResponse.setTransactionDate(new Date().toString());
        itemCheckinResponse.setUpdatedDate(new Date().toString());

        assertNotNull(itemCheckinResponse.getScreenMessage());
        assertNotNull(itemCheckinResponse.getAlertType());
        assertNotNull(itemCheckinResponse.getBibId());
        assertNotNull(itemCheckinResponse.getCallNumber());
        assertNotNull(itemCheckinResponse.getCollectionCode());
        assertNotNull(itemCheckinResponse.getCreatedDate());
        assertNotNull(itemCheckinResponse.getCurrencyType());
        assertNotNull(itemCheckinResponse.getDestinationLocation());
        assertNotNull(itemCheckinResponse.getDueDate());
        assertNotNull(itemCheckinResponse.getEsipDataIn());
        assertNotNull(itemCheckinResponse.getEsipDataOut());
        assertNotNull(itemCheckinResponse.getFeeAmount());
        assertNotNull(itemCheckinResponse.getFeeType());
        assertNotNull(itemCheckinResponse.getHoldPatronId());
        assertNotNull(itemCheckinResponse.getHoldPatronName());
        assertNotNull(itemCheckinResponse.getInstitutionID());
        assertNotNull(itemCheckinResponse.getISBN());
        assertNotNull(itemCheckinResponse.getItemBarcode());
        assertNotNull(itemCheckinResponse.getItemBarcodes());
        assertNotNull(itemCheckinResponse.getItemOwningInstitution());
        assertNotNull(itemCheckinResponse.getJobId());
        assertNotNull(itemCheckinResponse.getLCCN());
        assertNotNull(itemCheckinResponse.getMediaType());
        assertNotNull(itemCheckinResponse.getPatronIdentifier());
        assertNotNull(itemCheckinResponse.getPermanentLocation());
        assertNotNull(itemCheckinResponse.getSecurityInhibit());
        assertNotNull(itemCheckinResponse.getSortBin());
        assertNotNull(itemCheckinResponse.getTitleIdentifier());
        assertNotNull(itemCheckinResponse.getTransactionDate());
        assertNotNull(itemCheckinResponse.getUpdatedDate());
        assertTrue(itemCheckinResponse.isAlert());
        assertTrue(itemCheckinResponse.isMagneticMedia());
        assertTrue(itemCheckinResponse.isProcessed());
        assertTrue(itemCheckinResponse.isResensitize());
        assertTrue(itemCheckinResponse.isSuccess());
    }
}
