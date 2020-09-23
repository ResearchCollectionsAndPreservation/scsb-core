package org.recap.model.accession;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AccessionResponseUT {

    @Test
    public void getAccessionResponse(){
        AccessionResponse accessionResponse = new AccessionResponse();
        accessionResponse.setItemBarcode("123456");
        accessionResponse.setMessage("TEST");
        assertNotNull(accessionResponse.getItemBarcode());
        assertNotNull(accessionResponse.getMessage());
    }
}
