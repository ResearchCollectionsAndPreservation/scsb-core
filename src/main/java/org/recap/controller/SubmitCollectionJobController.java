package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by harikrishnanv on 20/6/17.
 */
@RestController
@RequestMapping("/submitCollectionJob")
public class SubmitCollectionJobController {

    private static final Logger logger = LoggerFactory.getLogger(SubmitCollectionJobController.class);

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private CamelContext camelContext;

    /**
     * This method is initiated from the scheduler to start the submit collection process in sequence
     * if the file exists in the respective folders.
     *
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/startSubmitCollection")
    public String startSubmitCollection() throws Exception{
        camelContext.getRouteController().startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_PUL_ROUTE);
        Endpoint endpoint = camelContext.getEndpoint(RecapConstants.SUBMIT_COLLECTION_COMPLETION_QUEUE_TO);
        PollingConsumer consumer = null;
        try {
            consumer = endpoint.createPollingConsumer();
            Exchange exchange = consumer.receive();
            logger.info("Message Received : {}", exchange.getIn().getBody());
        }
        catch (Exception e){
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
        finally {
            if(consumer != null) {
                consumer.close();
            }
        }

        logger.info("Submit Collection Job ends");
        return RecapCommonConstants.SUCCESS;
    }
}
