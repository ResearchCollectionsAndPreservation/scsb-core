package org.recap.camel.submitcollection.processor;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;

/**
 * Created by premkb on 20/3/17.
 */
public class SubmitCollectionNotificationProcessor {

    private ProducerTemplate producer;

    public void sendSubmitCollectionNotification(){
        producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION);
    }

    private EmailPayLoad getEmailPayLoad(){
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setSubject("Sub collec not");
        emailPayLoad.setMessageDisplay("sucess started");
        emailPayLoad.setTo("premlovesindia@gmail.com");
        return  emailPayLoad;
    }
}
