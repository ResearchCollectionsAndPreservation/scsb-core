package org.recap.ils.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class BibRecordUT {

    @Test
    public void getBibRecord(){
        BibRecord bibRecord = new BibRecord();
        Holdings holdings = new Holdings();
        Holding holding = new Holding();
        holding.setOwningInstitutionHoldingsId("1");
        holdings.setHolding(Arrays.asList(holding));
        bibRecord.setHoldings(Arrays.asList(holdings));
        bibRecord.setBib(new Bib());
        assertNotNull(holdings.getHolding());
        assertNotNull(bibRecord.getBib());
        assertNotNull(bibRecord.getHoldings());
    }
}
