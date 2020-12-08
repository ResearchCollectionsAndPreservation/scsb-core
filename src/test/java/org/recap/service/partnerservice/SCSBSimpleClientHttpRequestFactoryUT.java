package org.recap.service.partnerservice;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;

import java.io.IOException;
import java.net.HttpURLConnection;


public class SCSBSimpleClientHttpRequestFactoryUT extends BaseTestCaseUT {

    @InjectMocks
    SCSBSimpleClientHttpRequestFactory mockSCSBSimpleClientHttpRequestFactory;

    @Mock
    HttpURLConnection connection;

    @Test
    public void testprepareConnection() throws IOException {
        mockSCSBSimpleClientHttpRequestFactory.prepareConnection(connection,"");
    }
}
