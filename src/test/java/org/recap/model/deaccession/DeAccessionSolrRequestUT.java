package org.recap.model.deaccession;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class DeAccessionSolrRequestUT extends BaseTestCaseUT {

    @Test
    public void test() throws Exception {
        DeAccessionSolrRequest deAccessionSolrRequest = new DeAccessionSolrRequest();
        deAccessionSolrRequest.setItemIds(Arrays.asList(1,2,3));
        deAccessionSolrRequest.setBibIds(Arrays.asList(4,5,6));
        deAccessionSolrRequest.setHoldingsIds(Arrays.asList(7,8,9));
        deAccessionSolrRequest.setStatus(RecapCommonConstants.SUCCESS);
        assertTrue(deAccessionSolrRequest.getStatus().contains(RecapCommonConstants.SUCCESS));
    }
}
