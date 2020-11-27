package org.recap.camel.statusreconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.service.statusreconciliation.StatusReconciliationEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by hemalathas on 22/5/17.
 */
@Component
public class StatusReconciliationS3RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StatusReconciliationS3RouteBuilder.class);

    /**
     * Instantiates a new Status reconciliation ftp route builder.
     *
     * @param camelContext         the camel context
     * @param applicationContext   the application context
     * @param statusReconciliation the status reconciliation
     */
    @Autowired
    public StatusReconciliationS3RouteBuilder(CamelContext camelContext, ApplicationContext applicationContext, @Value("${status.reconciliation}") String statusReconciliation) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.STATUS_RECONCILIATION_REPORT)
                            .routeId(RecapConstants.STATUS_RECONCILIATION_REPORT_ID)
                            .onCompletion().onWhen(header(RecapConstants.FOR).isEqualTo(RecapConstants.STATUS_RECONCILIATION))
                            .log("status reconciliation process finished files generated in S3")
                            .bean(applicationContext.getBean(StatusReconciliationEmailService.class), RecapConstants.PROCESS_INPUT)
                            .end()
                            .choice()
                            .when(header(RecapConstants.FOR).isEqualTo(RecapConstants.STATUS_RECONCILIATION))
                            .marshal().bindy(BindyType.Csv, StatusReconciliationCSVRecord.class)
                            .setHeader(S3Constants.KEY, simple("archival/share/recap/status-reconciliation/StatusReconciliation-${date:now:yyyyMMdd_HHmmss}.csv"))
                            .to("aws-s3://{{scsbBucketName}}?autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                            .when(header(RecapConstants.FOR).isEqualTo(RecapConstants.STATUS_RECONCILIATION_FAILURE))
                            .setHeader(S3Constants.KEY, simple("archival/share/recap/status-reconciliation/StatusReconciliationFailure-${date:now:yyyyMMdd_HHmmss}.csv"))
                            .to("aws-s3://{{scsbBucketName}}?autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                            .marshal().bindy(BindyType.Csv, StatusReconciliationErrorCSVRecord.class)
                            .log("status reconciliation failure report generated in ftp");
                }
            });
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
    }
}
