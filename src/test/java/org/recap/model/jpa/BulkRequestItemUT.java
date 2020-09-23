package org.recap.model.jpa;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BulkRequestItemUT {

    @Test
    public void getBulkRequestItem(){
        BulkRequestItem bulkRequestItem = new BulkRequestItem();
        bulkRequestItem.setCustomerCode("1235");
        bulkRequestItem.setItemBarcode("24546");
        bulkRequestItem.setRequestId("1");
        bulkRequestItem.setRequestStatus("SUCCESS");
        bulkRequestItem.setStatus("Complete");
        assertNotNull(bulkRequestItem.getCustomerCode());
        assertNotNull(bulkRequestItem.getItemBarcode());
        assertNotNull(bulkRequestItem.getRequestId());
        assertNotNull(bulkRequestItem.getRequestStatus());
        assertNotNull(bulkRequestItem.getStatus());
    }
}
