package org.recap.model.deaccession;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class DeAccessionDBResponseEntityUT extends BaseTestCaseUT {

    @Test
    public void test() throws Exception {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("123456");
        deAccessionDBResponseEntity.setBibliographicIds(Arrays.asList(1,2,3));
        deAccessionDBResponseEntity.setCollectionGroupCode("Shared");
        deAccessionDBResponseEntity.setCustomerCode("PA");
        deAccessionDBResponseEntity.setDeliveryLocation("PA");
        deAccessionDBResponseEntity.setHoldingIds(Arrays.asList(4,5,6));
        deAccessionDBResponseEntity.setInstitutionCode("PUL");
        deAccessionDBResponseEntity.setItemId(1);
        deAccessionDBResponseEntity.setOwningInstitutionBibIds(Arrays.asList("1","2","3"));
        deAccessionDBResponseEntity.setItemStatus(RecapConstants.ITEM_STATUS_AVAILABLE);
        deAccessionDBResponseEntity.setReasonForFailure("");
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        assertNotNull(deAccessionDBResponseEntity);
    }
}
