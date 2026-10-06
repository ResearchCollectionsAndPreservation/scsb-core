package org.recap.service;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by sudhishk on 19/1/17.
 */
public class EmailServiceUT extends BaseTestCaseUT {

    @InjectMocks
    EmailService emailService;

    @Mock
    private ProducerTemplate producerTemplate;

    @Test
    public void sendEmail() {
        emailService.sendEmail("", "12345", ScsbConstants.DELETED_MAIL_TO, "");
        emailService.sendEmail("", "12345", "", "");
        assertNotNull(emailService);
    }


}
