package org.recap.model.jpa;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ItemRefileResponseUT {

    @Test
    public void getItemRefileResponse(){
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setJobId("1");
        itemRefileResponse.setRequestId(1);
        assertNotNull(itemRefileResponse.getJobId());
        assertNotNull(itemRefileResponse.getRequestId());
    }
}
