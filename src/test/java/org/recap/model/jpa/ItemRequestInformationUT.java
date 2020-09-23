package org.recap.model.jpa;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ItemRequestInformationUT {

    @Test
    public void getItemRequestInformation(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestType("RECALL");
        itemRequestInformation.setItemBarcodes(Arrays.asList("234785"));
        itemRequestInformation.setRequestingInstitution("CUL");
        itemRequestInformation.setUsername("test");
        itemRequestInformation.setBibId("13467");
        itemRequestInformation.setChapterTitle("Title");
        itemRequestInformation.setCustomerCode("PA");
        itemRequestInformation.setEmailAddress("test@gmail.com");
        itemRequestInformation.setPatronBarcode("23578");
        itemRequestInformation.setRequestId(1);
        itemRequestInformation.setTrackingId("33667889");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setDeliveryLocation("PA");
        itemRequestInformation.setTitleIdentifier("235667");
        itemRequestInformation.setAuthor("test");
        itemRequestInformation.setCallNumber("2123467");
        itemRequestInformation.setEddNotes("test");
        itemRequestInformation.setEndPage("45");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setIssue("test");
        itemRequestInformation.setItemAuthor("test");
        itemRequestInformation.setItemVolume("2");
        itemRequestInformation.setPickupLocation("PA");
        itemRequestInformation.setStartPage("23");
        itemRequestInformation.setVolume("45");
        itemRequestInformation.isOwningInstitutionItem();

        assertNotNull(itemRequestInformation.getAuthor());
        assertNotNull(itemRequestInformation.getBibId());
        assertNotNull(itemRequestInformation.getCallNumber());
        assertNotNull(itemRequestInformation.getChapterTitle());
        assertNotNull(itemRequestInformation.getCustomerCode());
        assertNotNull(itemRequestInformation.getDeliveryLocation());
        assertNotNull(itemRequestInformation.getEddNotes());
        assertNotNull(itemRequestInformation.getEmailAddress());
        assertNotNull(itemRequestInformation.getEndPage());
        assertNotNull(itemRequestInformation.getExpirationDate());
        assertNotNull(itemRequestInformation.getIssue());
        assertNotNull(itemRequestInformation.getItemAuthor());
        assertNotNull(itemRequestInformation.getItemBarcodes());
        assertNotNull(itemRequestInformation.getItemOwningInstitution());
        assertNotNull(itemRequestInformation.getItemVolume());
        assertNotNull(itemRequestInformation.getPatronBarcode());
        assertNotNull(itemRequestInformation.getPickupLocation());
        assertNotNull(itemRequestInformation.getRequestId());
        assertNotNull(itemRequestInformation.getRequestingInstitution());
        assertNotNull(itemRequestInformation.getRequestNotes());
        assertNotNull(itemRequestInformation.getRequestType());
        assertNotNull(itemRequestInformation.getStartPage());
        assertNotNull(itemRequestInformation.getTitleIdentifier());
        assertNotNull(itemRequestInformation.getTrackingId());
        assertNotNull(itemRequestInformation.getUsername());
        assertNotNull(itemRequestInformation.getVolume());
    }
}
