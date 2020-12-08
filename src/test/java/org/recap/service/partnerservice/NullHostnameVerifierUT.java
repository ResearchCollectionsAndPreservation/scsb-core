package org.recap.service.partnerservice;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;

import javax.net.ssl.SSLSession;

import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 7/7/17.
 */
public class NullHostnameVerifierUT extends BaseTestCaseUT {

    @InjectMocks
    NullHostnameVerifier nullHostnameVerifier;

    @Mock
    SSLSession session;

    @Test
    public void testNullHostnameVerifier(){
        boolean isVerified = nullHostnameVerifier.verify("test",session);
        assertTrue(isVerified);
    }

}