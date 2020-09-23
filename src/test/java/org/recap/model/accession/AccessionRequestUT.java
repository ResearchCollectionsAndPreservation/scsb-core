package org.recap.model.accession;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AccessionRequestUT {

    @Test
    public void getAccessionRequest(){
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("23455");
        accessionRequest.setItemBarcode("123456");
        assertNotNull(accessionRequest.getCustomerCode());
        assertNotNull(accessionRequest.getItemBarcode());
    }
}
