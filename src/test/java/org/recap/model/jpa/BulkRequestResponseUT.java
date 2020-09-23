package org.recap.model.jpa;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BulkRequestResponseUT {

    @Test
    public void getBulkRequestResponse(){
        BulkRequestResponse bulkRequestResponse = new BulkRequestResponse();
        bulkRequestResponse.setSuccess(true);
        bulkRequestResponse.setScreenMessage("SUCCESS");
        assertNotNull(bulkRequestResponse.getScreenMessage());
        assertNotNull(bulkRequestResponse.isSuccess());
    }
}
