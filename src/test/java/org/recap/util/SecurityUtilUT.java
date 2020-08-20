package org.recap.util;

import junit.framework.Assert;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 15/9/17.
 */

public class SecurityUtilUT extends BaseTestCase{

    @Autowired
    private SecurityUtil securityUtil;

    @Test
    public void getEncryptedValue(){
        String value = "test@mail.com";
        String encryptedValue = securityUtil.getEncryptedValue(value);
        String decryptedValue = securityUtil.getDecryptedValue(encryptedValue);
    }
}
