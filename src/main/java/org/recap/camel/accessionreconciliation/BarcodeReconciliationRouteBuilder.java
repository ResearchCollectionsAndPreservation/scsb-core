package org.recap.camel.accessionreconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.route.StartRouteProcessor;
import org.recap.camel.route.StopRouteProcessor;
import org.springframework.context.ApplicationContext;

public class BarcodeReconciliationRouteBuilder extends RouteBuilder {

    String institution;
    String accessionReconciliationFtpDir;
    String accessionReconciliationWorkDir;
    String accessionReconciliationFilePath;
    String ftpAccessionReconciliationProcessedDir;
    String ftpUserName;
    String ftpPrivateKey;
    String ftpKnownHost;
    ApplicationContext applicationContext;


    public BarcodeReconciliationRouteBuilder(ApplicationContext applicationContext,CamelContext camelContext, String ftpUserName, String ftpPrivateKey, String ftpKnownHost, String institution, String accessionReconciliationFtpDir, String accessionReconciliationWorkDir, String accessionReconciliationFilePath, String ftpAccessionReconciliationProcessedDir){
        super(camelContext);
        this.institution=institution;
        this.accessionReconciliationFtpDir=accessionReconciliationFtpDir;
        this.accessionReconciliationWorkDir=accessionReconciliationWorkDir;
        this.accessionReconciliationFilePath=accessionReconciliationFilePath;
        this.ftpAccessionReconciliationProcessedDir=ftpAccessionReconciliationProcessedDir;
        this.ftpUserName=ftpUserName;
        this.ftpPrivateKey=ftpPrivateKey;
        this.ftpKnownHost=ftpKnownHost;
        this.applicationContext=applicationContext;
    }

    @Override
    public void configure() throws Exception {
        Predicate gzipFile = exchange -> {
            if(exchange.getIn().getHeader("CamelAwsS3Key") != null) {
                String fileName = exchange.getIn().getHeader("CamelAwsS3Key").toString();
                return StringUtils.equalsIgnoreCase("gz", FilenameUtils.getExtension(fileName));
            }else
                return false;
        };
        from("aws-s3://{{awsS3BucketName}}?prefix=share/recap/accession-reconciliation/" + institution + "/&deleteAfterRead=false&sendEmptyMessageWhenIdle=true&autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
      // from(RecapCommonConstants.SFTP+ ftpUserName +  RecapCommonConstants.AT + accessionReconciliationFtpDir + RecapCommonConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapCommonConstants.KNOWN_HOST_FILE + ftpKnownHost+ RecapConstants.ACCESSION_RR_FTP_OPTIONS+accessionReconciliationWorkDir)
                .routeId(institution+"accessionReconcilationFtpRoute")
                .noAutoStartup()
                .choice()
                    .when(gzipFile)
                         .unmarshal().gzipDeflater()
                         .log(institution+" Accession Reconciliation FTP Route Unzip Complete")
                         .process(new StartRouteProcessor(institution+RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE))
                         .to(RecapConstants.DIRECT+ institution+RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                     .when(body().isNull())
                            .log("No File To Process For "+institution+" Accession Reconciliation")
                            .process(new StopRouteProcessor(institution+"accessionReconcilationFtpRoute"))
                    .otherwise()
                             .process(new StartRouteProcessor(institution+RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE))
                             .to(RecapConstants.DIRECT+ institution+RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                .endChoice();

        from(RecapConstants.DIRECT+ institution+RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                .routeId(institution+RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE)
                .noAutoStartup()
                .log("direct accession reconciliation "+institution+" started")
                .split(body().tokenize("\n",1000,true))
                .unmarshal().bindy(BindyType.Csv, BarcodeReconcilitaionReport.class)
                .bean(applicationContext.getBean(AccessionReconciliationProcessor.class, institution), RecapConstants.PROCESS_INPUT)
                .end()
                .onCompletion()
                .process(new StopRouteProcessor(institution+RecapConstants.ACCESSION_RECONCILIATION_DIRECT_ROUTE));
        from(RecapConstants.DAILY_RR_FS_FILE+accessionReconciliationFilePath+institution+ RecapConstants.DAILY_RR_FS_OPTIONS)
                .routeId(institution+"accessionReconcilationFsRoute")
                .noAutoStartup()
                //.to(RecapCommonConstants.SFTP+ ftpUserName +  RecapCommonConstants.AT + ftpAccessionReconciliationProcessedDir + RecapCommonConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapCommonConstants.KNOWN_HOST_FILE + ftpKnownHost+"&fileName=BarcodeReconciliation_"+institution+"_${date:now:yyyyMMdd_HHmmss}.csv")
                .setHeader(S3Constants.CONTENT_LENGTH, simple("${in.header.CamelFileLength}"))
                .setHeader(S3Constants.KEY,simple("share/recap/accession-reconciliation/archival/"+institution+"/BarcodeReconciliation_"+institution+"_${date:now:yyyyMMdd_HHmmss}.csv"))
                .to("aws-s3://{{scsbReportsBucket}}?autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                .onCompletion()
                .bean(applicationContext.getBean(AccessionReconciliationEmailService.class, institution), RecapConstants.PROCESS_INPUT)
                .process(new StopRouteProcessor(institution+"accessionReconcilationFsRoute"))
                .log("FS accession reconciliation "+institution+" completed");
    }
}
