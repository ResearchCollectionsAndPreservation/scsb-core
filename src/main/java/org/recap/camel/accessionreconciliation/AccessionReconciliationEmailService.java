package org.recap.camel.accessionreconciliation;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by akulak on 22/5/17.
 */
@Service
@Scope("prototype")
public class AccessionReconciliationEmailService {

    private static final Logger logger = LoggerFactory.getLogger(AccessionReconciliationEmailService.class);

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    PropertyUtil propertyUtil;

    private final String institutionCode;

    /**
     * Instantiates a new Accession reconciliation email service.
     *
     * @param institutionCode the institution code
     */
    public AccessionReconciliationEmailService(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    /**
     * Process input for accession reconciliation email service.
     *
     * @param exchange the exchange
     */
    public void processInput(Exchange exchange) {
        logger.info("accession email started for {}", institutionCode);
        producerTemplate.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(exchange), RecapConstants.EMAIL_BODY_FOR, "AccessionReconcilation");
    }

    /**
     * Get email pay load for accession reconciliation email service.
     *
     * @return the email pay load
     */
    public EmailPayLoad getEmailPayLoad(Exchange exchange) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        String fileNameWithPath = (String) exchange.getIn().getHeader(RecapConstants.CAMEL_AWS_KEY);
        emailPayLoad.setTo(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "email.accession.reconciliation.to"));
        emailPayLoad.setCc(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "email.accession.reconciliation.cc"));
        logger.info("Accession Reconciliation email sent to : {} and cc : {} ", emailPayLoad.getTo(), emailPayLoad.getCc());
        emailPayLoad.setMessageDisplay("Barcode Reconciliation has been completed for " + institutionCode.toUpperCase() + ". The report is at the FTP location " + fileNameWithPath);
        return emailPayLoad;
    }

}
