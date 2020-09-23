package org.recap.ils.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class HoldingUT {

    @Test
    public void getHolding(){
        Holding holding = new Holding();
        Items items = new Items();
        items.setContent(new ContentType());
        assertNotNull(items.getContent());
        holding.setContent(new ContentType());
        holding.setItems(Arrays.asList(items));
        holding.setOwningInstitutionHoldingsId("2345");
        assertNotNull(holding.getContent());
        assertNotNull(holding.getItems());
        assertNotNull(holding.getOwningInstitutionHoldingsId());
    }
}

