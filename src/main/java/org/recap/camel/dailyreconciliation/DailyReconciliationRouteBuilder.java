package org.recap.camel.dailyreconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.camel.route.StartRouteProcessor;
import org.recap.camel.route.StopRouteProcessor;
import org.recap.model.csv.DailyReconcilationRecord;
import org.springframework.context.ApplicationContext;

/**
 * Created by akulak on 3/5/17.
 */
public class DailyReconciliationRouteBuilder extends RouteBuilder {

    private ApplicationContext applicationContext;
    private String imsLocation;
    String dailyReconciliationS3;
    String dailyReconciliationFtpProcessed;
    String dailyReconciliationFilePath;

    /**
     * Instantiates a new Daily reconciliation route builder.
     *
     * @param camelContext                    the camel context
     * @param applicationContext              the application context
     * @param dailyReconciliationS3           the daily reconciliation s3
     * @param dailyReconciliationFtpProcessed the daily reconciliation s3 processed
     * @param dailyReconciliationFilePath     the daily reconciliation file path
     */
    public DailyReconciliationRouteBuilder(CamelContext camelContext, ApplicationContext applicationContext, String imsLocation, String dailyReconciliationS3, String dailyReconciliationFtpProcessed, String dailyReconciliationFilePath) {
        super(camelContext);
        this.applicationContext = applicationContext;
        this.imsLocation = imsLocation;
        this.dailyReconciliationS3 = dailyReconciliationS3;
        this.dailyReconciliationFtpProcessed = dailyReconciliationFtpProcessed;
        this.dailyReconciliationFilePath = dailyReconciliationFilePath;
    }

    @Override
    public void configure() throws Exception {

        /**
         * Predicate to identify is the input file is gz
         */
        Predicate gzipFile = exchange -> {
            if (exchange.getIn().getHeader(RecapConstants.CAMEL_AWS_KEY) != null) {
                String fileName = exchange.getIn().getHeader(RecapConstants.CAMEL_AWS_KEY).toString();
                return StringUtils.equalsIgnoreCase("gz", FilenameUtils.getExtension(fileName));
            } else {
                return false;
            }
        };

        from("aws-s3://{{scsbBucketName}}?prefix=" + dailyReconciliationS3 + imsLocation + "/{{s3DataFeedFileNamePrefix}}&deleteAfterRead=false&sendEmptyMessageWhenIdle=true&autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                .routeId(RecapConstants.DAILY_RR_S3_ROUTE_ID + imsLocation)
                .noAutoStartup()
                .log("Daily Reconciliation started for IMS Location : " + imsLocation)
                .choice()
                .when(gzipFile)
                .unmarshal().
                gzipDeflater()
                .log("Unzip process completed for daily reconciliation file for IMS Location : " + imsLocation)
                .process(new StartRouteProcessor(RecapConstants.PROCESS_DAILY_RECONCILIATION + imsLocation))
                .to(RecapConstants.DIRECT + RecapConstants.PROCESS_DAILY_RECONCILIATION + imsLocation)
                .when(body().isNull())
                .process(new StopRouteProcessor(RecapConstants.DAILY_RR_S3_ROUTE_ID + imsLocation))
                .log("No File To Process Daily Reconciliation for Ims Location " + imsLocation)
                .otherwise()
                .process(new StartRouteProcessor(RecapConstants.PROCESS_DAILY_RECONCILIATION + imsLocation))
                .to(RecapConstants.DIRECT + RecapConstants.PROCESS_DAILY_RECONCILIATION + imsLocation)
                .endChoice();

        from(RecapConstants.DIRECT + RecapConstants.PROCESS_DAILY_RECONCILIATION + imsLocation)
                .unmarshal().bindy(BindyType.Csv, DailyReconcilationRecord.class)
                .bean(applicationContext.getBean(DailyReconciliationProcessor.class, imsLocation), RecapConstants.PROCESS_INPUT)
                .end()
                .onCompletion()
                .process(new StopRouteProcessor(RecapConstants.PROCESS_DAILY_RECONCILIATION + imsLocation));

        from(RecapConstants.DAILY_RR_FS_FILE + dailyReconciliationFilePath + "/" + imsLocation + RecapConstants.DAILY_RR_FS_OPTIONS)
                .routeId(RecapConstants.DAILY_RR_FS_ROUTE_ID + imsLocation)
                .noAutoStartup()
                .setHeader(S3Constants.CONTENT_LENGTH, simple("${in.header.CamelFileLength}"))
                .setHeader(S3Constants.KEY, simple(dailyReconciliationFtpProcessed + imsLocation + "/DailyReconciliation_" + imsLocation +"_${date:now:yyyyMMdd_HHmmss}.xlsx"))
                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                .onCompletion()
                .log("Email service started for daily reconciliation for IMS Location : " + imsLocation)
                .bean(applicationContext.getBean(DailyReconciliationEmailService.class, imsLocation))
                .process(new StopRouteProcessor(RecapConstants.DAILY_RR_FS_ROUTE_ID + imsLocation))
                .log("Daily Reconciliation completed for IMS Location: " + imsLocation);


    }
}
