package org.recap.model.request;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class ItemCheckInRequestUT {

    @Test
    public void getItemCheckInRequest(){
        ItemCheckInRequest itemCheckInRequest = new ItemCheckInRequest();
        itemCheckInRequest.setItemBarcodes(Arrays.asList("232467"));
        itemCheckInRequest.setItemOwningInstitution("PUL");
        itemCheckInRequest.setPatronIdentifier("4356882");

        assertNotNull(itemCheckInRequest.getItemBarcodes());
        assertNotNull(itemCheckInRequest.getItemOwningInstitution());
        assertNotNull(itemCheckInRequest.getPatronIdentifier());
    }
}
