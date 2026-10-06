package org.recap.model.deaccession;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeAccessionDBResponseEntityUT extends BaseTestCaseUT {

    @Test
    public void test() throws Exception {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("123456");
        deAccessionDBResponseEntity.setBibliographicIds(Arrays.asList(1, 2, 3));
        deAccessionDBResponseEntity.setCollectionGroupCode("Shared");
        deAccessionDBResponseEntity.setCustomerCode("PA");
        deAccessionDBResponseEntity.setDeliveryLocation("PA");
        deAccessionDBResponseEntity.setHoldingIds(Arrays.asList(4, 5, 6));
        deAccessionDBResponseEntity.setInstitutionCode("PUL");
        deAccessionDBResponseEntity.setItemId(1);
        deAccessionDBResponseEntity.setOwningInstitutionBibIds(Arrays.asList("1", "2", "3"));
        deAccessionDBResponseEntity.setItemStatus(ScsbConstants.ITEM_STATUS_AVAILABLE);
        deAccessionDBResponseEntity.setReasonForFailure("");
        deAccessionDBResponseEntity.setStatus(ScsbCommonConstants.SUCCESS);
        assertNotNull(deAccessionDBResponseEntity);
    }
}
