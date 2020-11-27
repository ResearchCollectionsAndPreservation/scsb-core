package org.recap.camel.dailyreconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.camel.route.StopRouteProcessor;
import org.recap.model.csv.DailyReconcilationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by akulak on 3/5/17.
 */
@Component
public class DailyReconciliationRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DailyReconciliationRouteBuilder.class);

    /**
     * Instantiates a new Daily reconcilation route builder.
     *
     * @param camelContext                   the camel context
     * @param applicationContext             the application context
     * @param dailyReconciliationFtp          the daily reconciliation ftp
     * @param dailyReconciliationFtpProcessed the daily reconciliation ftp processed
     * @param filePath                       the file path
     */

    /**
     * Predicate to identify is the input file is gz
     */
    Predicate gzipFile = new Predicate() {
        @Override
        public boolean matches(Exchange exchange) {

        String fileName = (String) exchange.getIn().getHeader(Exchange.FILE_NAME);
        return StringUtils.equalsIgnoreCase("gz", FilenameUtils.getExtension(fileName));

        }
    };

    public DailyReconciliationRouteBuilder(CamelContext camelContext, ApplicationContext applicationContext, @Value("${ftp.daily.reconciliation}") String dailyReconciliationFtp,
                                           @Value("${ftp.daily.reconciliation.processed}") String dailyReconciliationFtpProcessed,
                                           @Value("${daily.reconciliation.file}") String filePath) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("aws-s3://{{scsbBucketName}}?prefix=share/recap/daily-reconciliation&deleteAfterRead=false&sendEmptyMessageWhenIdle=true&autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                            .routeId(RecapConstants.DAILY_RR_FTP_ROUTE_ID)
                            .noAutoStartup()
                            .log("daily reconciliation started")
                            .choice()
                            .when(gzipFile)
                            .unmarshal().
                            gzipDeflater()
                .log("Unzip processed completed for daily reconciliation file")
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                String fileName = (String)exchange.getIn().getHeader(Exchange.FILE_NAME);
                                exchange.getIn().setHeader(Exchange.FILE_NAME, fileName.replaceFirst(".gz", ".csv"));
                                }
                            })
                            .to(RecapConstants.DIRECT+ RecapConstants.PROCESS_DAILY_RECONCILIATION)
                            .otherwise()
                            .to(RecapConstants.DIRECT+ RecapConstants.PROCESS_DAILY_RECONCILIATION)
                            .end()
                            .onCompletion()
                            .choice()
                            .when(exchangeProperty(RecapCommonConstants.CAMEL_BATCH_COMPLETE))
                            .log("Stopping DailyReconciliation Process")
                            .process(new StopRouteProcessor(RecapConstants.DAILY_RR_FTP_ROUTE_ID));

                    from(RecapConstants.DIRECT+ RecapConstants.PROCESS_DAILY_RECONCILIATION)
                            .unmarshal().bindy(BindyType.Csv, DailyReconcilationRecord.class)
                            .bean(applicationContext.getBean(DailyReconciliationProcessor.class), RecapConstants.PROCESS_INPUT)
                            .end();
                }
            });



            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.DAILY_RR_FS_FILE+filePath+ RecapConstants.DAILY_RR_FS_OPTIONS)
                            .routeId(RecapConstants.DAILY_RR_FS_ROUTE_ID)
                            .noAutoStartup()
                            .setHeader(S3Constants.CONTENT_LENGTH, simple("${in.header.CamelFileLength}"))
                            .setHeader(S3Constants.KEY,simple("reports/share/recap/daily-reconciliation/BarcodeReconciliation_${date:now:yyyyMMdd_HHmmss}.csv"))
                            .to("aws-s3://{{scsbBucketName}}?autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                            .onCompletion()
                            .log("email service started for daily reconciliation")
                            .bean(applicationContext.getBean(DailyReconciliationEmailService.class))
                            .process(new StopRouteProcessor(RecapConstants.DAILY_RR_FS_ROUTE_ID));
                }
            });

        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }

    }
}
