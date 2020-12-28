package org.recap.service;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by sudhishk on 19/1/17.
 */
@Service
public class EmailService {

    @Value("${email.deleted.records.to}")
    private String deletedRecordsMailTo;

    @Autowired
    private ProducerTemplate producer;

    /**
     *  Send email method for deleted records reporting.
     *
     * @param messageDisplay
     * @param patronBarcode
     * @param toInstitution
     * @param subject
     */
    public void sendEmail(String messageDisplay, String patronBarcode, String toInstitution, String subject) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setTo(emailIdTo(toInstitution));
        emailPayLoad.setMessageDisplay(messageDisplay);
        emailPayLoad.setPatronBarcode(patronBarcode);
        emailPayLoad.setSubject(subject);
        producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.EMAIL_BODY_FOR, RecapConstants.DELETED_MAIL_QUEUE);
    }

    /**
     * @param institution
     * @return
     */
    private String emailIdTo(String institution) {
        if(institution.equalsIgnoreCase(RecapConstants.DELETED_MAIL_TO)){
            return deletedRecordsMailTo;
        }
        return null;
    }
}
