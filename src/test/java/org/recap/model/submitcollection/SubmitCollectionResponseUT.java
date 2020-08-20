package org.recap.model.submitcollection;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SubmitCollectionResponseUT {

    @Test
    public void getSubmitCollectionResponse(){
        SubmitCollectionResponse submitCollectionResponse = new SubmitCollectionResponse();
        submitCollectionResponse.setItemBarcode("123445");
        submitCollectionResponse.setMessage("Success");
        assertNotNull(submitCollectionResponse.getMessage());
        assertNotNull(submitCollectionResponse.getItemBarcode());
    }
}
