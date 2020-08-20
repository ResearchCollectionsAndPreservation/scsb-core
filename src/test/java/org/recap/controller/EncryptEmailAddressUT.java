package org.recap.controller;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class EncryptEmailAddressUT extends BaseTestCase {

    @Autowired
    EncryptEmailAddress encryptEmailAddress;
    @Test
    public void startEncryptEmailAddress(){
        String result = encryptEmailAddress.startEncryptEmailAddress();
        assertNotNull(result);
    }
}
