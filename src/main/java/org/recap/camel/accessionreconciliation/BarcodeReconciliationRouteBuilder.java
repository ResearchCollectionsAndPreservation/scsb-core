package org.recap.camel.accessionreconciliation;

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
import org.springframework.context.ApplicationContext;

public class BarcodeReconciliationRouteBuilder extends RouteBuilder {

    String institution;
    String imsLocation;
    String accessionReconciliationS3Dir;
    String accessionReconciliationFilePath;
    String s3AccessionReconciliationProcessedDir;
    ApplicationContext applicationContext;


    public BarcodeReconciliationRouteBuilder(ApplicationContext applicationContext, CamelContext camelContext, String institution, String imsLocation, String accessionReconciliationS3Dir, String accessionReconciliationFilePath, String s3AccessionReconciliationProcessedDir) {
        super(camelContext);
        this.institution = institution;
        this.imsLocation = imsLocation;
        this.accessionReconciliationS3Dir = accessionReconciliationS3Dir;
        this.accessionReconciliationFilePath = accessionReconciliationFilePath;
        this.s3AccessionReconciliationProcessedDir = s3AccessionReconciliationProcessedDir;
        this.applicationContext = applicationContext;
    }

    @Override
    public void configure() throws Exception {
        Predicate gzipFile = exchange -> {
            if (exchange.getIn().getHeader(RecapConstants.CAMEL_AWS_KEY) != null) {
                String fileName = exchange.getIn().getHeader(RecapConstants.CAMEL_AWS_KEY).toString();
                return StringUtils.equalsIgnoreCase("gz", FilenameUtils.getExtension(fileName));
            } else
                return false;
        };
        from("aws-s3://{{scsbBucketName}}?prefix="+accessionReconciliationS3Dir + imsLocation + "/" + institution + "/{{s3DataFeedFileNamePrefix}}&deleteAfterRead=false&sendEmptyMessageWhenIdle=true&autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                .routeId(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_S3_ROUTE_ID)
                .noAutoStartup()
                .choice()
                .when(gzipFile)
                .unmarshal().gzipDeflater()
                .log(imsLocation + " - " + institution + " Accession Reconciliation FTP Route Unzip Complete")
                .process(new StartRouteProcessor(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE))
                .to(RecapConstants.DIRECT + imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                .when(body().isNull())
                .log("No File To Process For " + imsLocation + " - " + institution + " Accession Reconciliation")
                .process(new StopRouteProcessor(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_S3_ROUTE_ID))
                .otherwise()
                .process(new StartRouteProcessor(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE))
                .to(RecapConstants.DIRECT + imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                .endChoice();

        from(RecapConstants.DIRECT + imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                .routeId(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                .noAutoStartup()
                .log("direct accession reconciliation " + imsLocation + " - " + institution + " started")
                .split(body().tokenize("\n", 1000, true))
                .unmarshal().bindy(BindyType.Csv, BarcodeReconcilitaionReport.class)
                .bean(applicationContext.getBean(AccessionReconciliationProcessor.class, institution, imsLocation), RecapConstants.PROCESS_INPUT)
                .end()
                .onCompletion()
                .process(new StopRouteProcessor(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE));
        from(RecapConstants.DAILY_RR_FS_FILE + accessionReconciliationFilePath + "/" + imsLocation + "/" + institution + RecapConstants.DAILY_RR_FS_OPTIONS)
                .routeId(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_FS_ROUTE_ID)
                .noAutoStartup()
                .setHeader(S3Constants.CONTENT_LENGTH, simple("${in.header.CamelFileLength}"))
                .setHeader(S3Constants.KEY, simple(s3AccessionReconciliationProcessedDir + imsLocation + "/" + institution + "/BarcodeReconciliation_" + imsLocation + "_" + institution + "_${date:now:yyyyMMdd_HHmmss}.csv"))
                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                .onCompletion()
                .bean(applicationContext.getBean(AccessionReconciliationEmailService.class, institution, imsLocation), RecapConstants.PROCESS_INPUT)
                .process(new StopRouteProcessor(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_FS_ROUTE_ID))
                .log("FS accession reconciliation " + imsLocation + " - " + institution + " completed");
    }
}
