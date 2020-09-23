package org.recap.model.jpa;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ReplaceRequestUT {

    @Test
    public void getReplaceRequest(){
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType("RECALL");
        replaceRequest.setToDate(new Date().toString());
        replaceRequest.setStartRequestId("1");
        replaceRequest.setRequestStatus("Pending");
        replaceRequest.setFromDate(new Date().toString());
        replaceRequest.setEndRequestId("10");

        assertNotNull(replaceRequest.getRequestIds());
        assertNotNull(replaceRequest.getToDate());
        assertNotNull(replaceRequest.getReplaceRequestByType());
        assertNotNull(replaceRequest.getEndRequestId());
        assertNotNull(replaceRequest.getFromDate());
        assertNotNull(replaceRequest.getRequestStatus());
        assertNotNull(replaceRequest.getStartRequestId());
    }
}

