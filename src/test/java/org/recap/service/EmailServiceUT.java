package org.recap.service;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;

import static org.junit.Assert.assertNotNull;

/**
 * Created by sudhishk on 19/1/17.
 */
public class EmailServiceUT extends BaseTestCaseUT {

    @InjectMocks
    EmailService emailService;

    @Mock
    private ProducerTemplate producerTemplate;

    @Test
    public void sendEmail(){
        emailService.sendEmail("","12345", RecapConstants.DELETED_MAIl_TO,"");
        emailService.sendEmail("","12345", "","");
        assertNotNull(emailService);
    }


}
