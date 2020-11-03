package org.recap.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

/**
 * Created by akulak on 19/7/17.
 */
@Scope("prototype")
public class StartRouteProcessor implements Processor {

    private static final Logger log= LoggerFactory.getLogger(StartRouteProcessor.class);

    private String routeId;

    public StartRouteProcessor(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Starting next route !!! {}",routeId);
        exchange.getContext().getRouteController().startRoute(routeId);
    }
}
