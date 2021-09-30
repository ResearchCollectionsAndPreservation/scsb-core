package org.recap.authorization;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.service.authorization.OauthTokenApiService;

public class OauthTokenApiServiceUT extends BaseTestCaseUT {

    @InjectMocks
    OauthTokenApiService oauthTokenApiService;

    @Test
    public void generateAccessToken() throws Exception {
        String oauthTokenApiUrl = "test";
        String operatorUserId = "test";
        String operatorPassword ="test";
        try {
            oauthTokenApiService.generateAccessToken(oauthTokenApiUrl,operatorUserId,operatorPassword);
        }catch (Exception e){}
    }
}
