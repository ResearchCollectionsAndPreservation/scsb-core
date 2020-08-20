package org.recap.camel.submitcollection.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.file.remote.SftpEndpoint;
import org.slf4j.Logger;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.camel.EmailPayLoad;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by harikrishnanv on 13/7/17.
 */
@Service
@Scope("prototype")
public class StartNextRoute implements Processor{

    @Autowired
    CamelContext camelContext;

    @Autowired
    private ProducerTemplate producer;

    @Value("${submit.collection.email.subject.for.empty.directory}")
    private String submitCollectionEmailSubjectForEmptyDirectory;

    @Value("${submit.collection.nofiles.email.pul.to}")
    private String emailToPUL;

    @Value("${submit.collection.nofiles.email.cul.to}")
    private String emailToCUL;

    @Value("${submit.collection.nofiles.email.nypl.to}")
    private String emailToNYPL;


    private static final Logger logger = LoggerFactory.getLogger(StartNextRoute.class);
    private String routeId;

    public StartNextRoute(String routeId) {
        this.routeId = routeId;
    }

    /**
     * This method is used to start the next route in sequence.
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
       if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_PUL_ROUTE)){
           camelContext.getRouteController().startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_PUL_ROUTE);
       }
       else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_PUL_ROUTE)){
           camelContext.getRouteController().startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_CUL_ROUTE);
       }
       else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_CUL_ROUTE)){
           camelContext.getRouteController().startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_CUL_ROUTE);
       }
       else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_CUL_ROUTE)){
           camelContext.getRouteController().startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_NYPL_ROUTE);
       }
       else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_NYPL_ROUTE)){
           camelContext.getRouteController().startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_NYPL_ROUTE);
       }
       else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_NYPL_ROUTE)){
           logger.info("SubmitCollection Sequence completed");
       }
    }

    /**
     * This method is used to send email when there are no files in the respective directory
     * @param exchange
     * @throws Exception
     */
    /* TO DO Need to fix getEndpointConfiguration() and uncomment the code.
    
     */
    public void sendEmailForEmptyDirectory(Exchange exchange) throws Exception {
        //String ftpLocationPath = (String) exchange.getFromEndpoint().getEndpointConfiguration().getParameter("path");
        //String ftpLocationPath = EndpointHelper.resolveParameter(camelContext, "path", exchange.getFromEndpoint().getClass()).getEndpointUri();
        String ftpLocationPath = ((SftpEndpoint) exchange.getFromEndpoint()).getConfiguration().getDirectoryName();
        if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_PUL_ROUTE )){
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(RecapCommonConstants.PRINCETON,ftpLocationPath), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES);
            logger.info("Email Sent");
        }
        else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_PUL_ROUTE)){
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(RecapCommonConstants.PRINCETON, ftpLocationPath), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES);
        }
        else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_CUL_ROUTE)){
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(RecapCommonConstants.COLUMBIA, ftpLocationPath), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES);
        }
        else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_CUL_ROUTE)){
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(RecapCommonConstants.COLUMBIA, ftpLocationPath), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES);
        }
        else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_NYPL_ROUTE)){
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(RecapCommonConstants.NYPL, ftpLocationPath), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES);
        }
        else if(routeId.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_NOT_PROTECTED_NYPL_ROUTE)){
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(RecapCommonConstants.NYPL, ftpLocationPath), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES);
        }
    }

    private EmailPayLoad getEmailPayLoad(String institutionCode, String ftpLocationPath) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setSubject(submitCollectionEmailSubjectForEmptyDirectory);
        emailPayLoad.setLocation(ftpLocationPath);
        if(RecapCommonConstants.PRINCETON.equalsIgnoreCase(institutionCode)){
            emailPayLoad.setTo(emailToPUL);
        } else if(RecapCommonConstants.COLUMBIA.equalsIgnoreCase(institutionCode)){
            emailPayLoad.setTo(emailToCUL);
        } else if(RecapCommonConstants.NYPL.equalsIgnoreCase(institutionCode)){
            emailPayLoad.setTo(emailToNYPL);
        }
        return  emailPayLoad;
    }

}
