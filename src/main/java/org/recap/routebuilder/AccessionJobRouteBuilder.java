package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.service.accession.AccessionJobProcessor;
import org.recap.controller.SharedCollectionRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by rajeshbabuk on 21/8/17.
 */
@Component
public class AccessionJobRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AccessionJobRouteBuilder.class);

    /**
     * Instantiates a new Accession job route builder.
     *
     * @param camelContext                   the camel context
     * @param sharedCollectionRestController the shared collection rest controller
     */
    @Autowired
    public AccessionJobRouteBuilder(CamelContext camelContext, ApplicationContext applicationContext, SharedCollectionRestController sharedCollectionRestController) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    onException(Exception.class)
                            .log("Exception caught during ongoing Accession Job")
                            .handled(true)
                            .to(RecapCommonConstants.DIRECT_ROUTE_FOR_EXCEPTION);
                    from(RecapCommonConstants.ACCESSION_JOB_INITIATE_QUEUE)
                            .routeId(RecapConstants.ACCESSION_JOB_INITIATE_ROUTE_ID)
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) {
                                    String jobId = null;
                                    try {
                                        jobId = (String) exchange.getIn().getBody();
                                        logger.info("Accession Job initiated for Job Id : {}", jobId);
                                        String accessionJobStatus = sharedCollectionRestController.ongoingAccessionJob(exchange);

                                        logger.info("Job Id : {} Accession Job Status : {}", jobId, accessionJobStatus);
                                        exchange.getIn().setBody("JobId:" + jobId + "|" + accessionJobStatus);
                                    } catch (Exception ex) {
                                        exchange.getIn().setBody("JobId:" + jobId + "|" + ex.getMessage());
                                        logger.info(RecapCommonConstants.LOG_ERROR, ex);
                                    }
                                }
                            })
                            .onCompletion()
                            .to(RecapCommonConstants.ACCESSION_JOB_COMPLETION_OUTGOING_QUEUE)
                            .end();
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapCommonConstants.DIRECT_ROUTE_FOR_EXCEPTION)
                            .log("Calling direct route for exception")
                            .bean(applicationContext.getBean(AccessionJobProcessor.class), RecapConstants.ACCESSION_CAUGHT_EXCEPTION_METHOD)
                            .onCompletion()
                            .to(RecapCommonConstants.ACCESSION_JOB_COMPLETION_OUTGOING_QUEUE);
                }
            });
        } catch (Exception ex) {
            logger.error(RecapConstants.EXCEPTION, ex);
        }
    }
}
