package org.recap.model.deaccession;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class DeAccessionDBResponseEntityUT {

    @Test
    public void getDeAccessionDBResponseEntity(){
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBibliographicIds(Arrays.asList(123456,231456));
        deAccessionDBResponseEntity.setHoldingIds(Arrays.asList(237789,456788));
        deAccessionDBResponseEntity.setItemId(1);
        deAccessionDBResponseEntity.setItemStatus("Success");
        deAccessionDBResponseEntity.setBarcode("12356");
        deAccessionDBResponseEntity.setCollectionGroupCode("PA");
        deAccessionDBResponseEntity.setCustomerCode("CU22567");
        deAccessionDBResponseEntity.setDeliveryLocation("PG");
        deAccessionDBResponseEntity.setInstitutionCode("2");
        deAccessionDBResponseEntity.setOwningInstitutionBibIds(Arrays.asList("1345576"));
        deAccessionDBResponseEntity.setReasonForFailure("Failure test");
        deAccessionDBResponseEntity.setStatus("Failure test");

        assertNotNull(deAccessionDBResponseEntity.getBibliographicIds());
        assertNotNull(deAccessionDBResponseEntity.getHoldingIds());
        assertNotNull(deAccessionDBResponseEntity.getItemId());
        assertNotNull(deAccessionDBResponseEntity.getItemStatus());
        assertNotNull(deAccessionDBResponseEntity.getBarcode());
        assertNotNull(deAccessionDBResponseEntity.getCollectionGroupCode());
        assertNotNull(deAccessionDBResponseEntity.getCustomerCode());
        assertNotNull(deAccessionDBResponseEntity.getDeliveryLocation());
        assertNotNull(deAccessionDBResponseEntity.getInstitutionCode());
        assertNotNull(deAccessionDBResponseEntity.getOwningInstitutionBibIds());
        assertNotNull(deAccessionDBResponseEntity.getReasonForFailure());
        assertNotNull(deAccessionDBResponseEntity.getStatus());

    }

}
