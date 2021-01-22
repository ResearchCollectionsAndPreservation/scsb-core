package org.recap.service.statusreconciliation;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by hemalathas on 31/5/17.
 */
@Service
@Scope("prototype")
@Slf4j
public class StatusReconciliationEmailService {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Value("${email.status.reconciliation.to}")
    private String statusReconciliationEmailTo;

    @Value("${email.status.reconciliation.cc}")
    private String statusReconciliationEmailCC;

    /**
     * Sets the email payload for the status reconciliation.
     *
     * @param exchange the exchange
     */
    public void processInput(Exchange exchange) {
        String fileLocation = (String) exchange.getIn().getHeader(RecapConstants.CAMEL_AWS_KEY);
        producerTemplate.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(fileLocation), RecapConstants.EMAIL_BODY_FOR, RecapConstants.STATUS_RECONCILIATION);
    }

    private EmailPayLoad getEmailPayLoad(String fileLocation) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setCc(statusReconciliationEmailCC);
        emailPayLoad.setTo(statusReconciliationEmailTo);
        log.info("Status Reconciliation : email sent to : {} and cc : {} ", emailPayLoad.getTo(), emailPayLoad.getCc());
        emailPayLoad.setMessageDisplay("The \"Out\" Status Reconciliation report is available at the S3 location " + fileLocation);
        return emailPayLoad;
    }
}
