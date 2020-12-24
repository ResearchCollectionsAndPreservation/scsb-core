package org.recap.model.request;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ItemCheckinResponseUT extends BaseTestCaseUT {

    @Test
    public void test() throws Exception {
        ItemCheckinResponse itemCheckinResponse=new ItemCheckinResponse();
        itemCheckinResponse.setItemBarcode("123456");
        itemCheckinResponse.setItemOwningInstitution("PUL");
        itemCheckinResponse.setAlert(true);
        itemCheckinResponse.setCreatedDate(new Date().toString());
        itemCheckinResponse.setBibId("1");
        itemCheckinResponse.setCollectionCode("Shared");
        itemCheckinResponse.setISBN("74578375");
        itemCheckinResponse.setCreatedDate(new Date().toString());
        itemCheckinResponse.setAlertType("alert");
        itemCheckinResponse.setInstitutionID("1");
        itemCheckinResponse.setHoldPatronId("4");
        itemCheckinResponse.setItemBarcodes(Arrays.asList("123456","234567"));
        itemCheckinResponse.setLCCN("58475");
        itemCheckinResponse.setScreenMessage(RecapCommonConstants.SUCCESS);
        itemCheckinResponse.setProcessed(true);
        itemCheckinResponse.setSecurityInhibit("security");
        itemCheckinResponse.setFeeAmount("1000");
        itemCheckinResponse.setPermanentLocation("PA");
        itemCheckinResponse.setSuccess(true);
        itemCheckinResponse.setJobId("445");
        itemCheckinResponse.setTransactionDate(new Date().toString());
        itemCheckinResponse.setResensitize(true);
        itemCheckinResponse.setEsipDataIn("in");
        itemCheckinResponse.setEsipDataOut("out");
        assertNotNull(itemCheckinResponse);
    }
}
