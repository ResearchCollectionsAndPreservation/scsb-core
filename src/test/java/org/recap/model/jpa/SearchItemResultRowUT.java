package org.recap.model.jpa;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 20/3/17.
 */
public class SearchItemResultRowUT {

    @Test
    public void testSearchItemResultRow(){

        SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setCallNumber("X");
        searchItemResultRow.setChronologyAndEnum("test");
        searchItemResultRow.setCustomerCode("PB");
        searchItemResultRow.setBarcode("3216598422355545");
        searchItemResultRow.setUseRestriction("Others");
        searchItemResultRow.setCollectionGroupDesignation("Open");
        searchItemResultRow.setAvailability("Available");
        searchItemResultRow.setSelectedItem(false);
        searchItemResultRow.setItemId(1);
        searchItemResultRow.compareTo(searchItemResultRow);
        searchItemResultRow.equals(searchItemResultRow);
        searchItemResultRow.hashCode();
        SearchItemResultRow searchItemResultRow1 = new SearchItemResultRow();
        searchItemResultRow1.equals(searchItemResultRow);
        assertNotNull(searchItemResultRow.getCallNumber());
        assertNotNull(searchItemResultRow.getChronologyAndEnum());
        assertNotNull(searchItemResultRow.getCustomerCode());
        assertNotNull(searchItemResultRow.getBarcode());
        assertNotNull(searchItemResultRow.getUseRestriction());
        assertNotNull(searchItemResultRow.getCollectionGroupDesignation());
        assertNotNull(searchItemResultRow.getAvailability());
        assertNotNull(searchItemResultRow.isSelectedItem());
        assertNotNull(searchItemResultRow.getItemId());

    }


}