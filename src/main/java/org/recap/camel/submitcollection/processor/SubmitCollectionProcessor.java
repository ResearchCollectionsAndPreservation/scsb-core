package org.recap.camel.submitcollection.processor;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws.s3.S3Endpoint;
import org.apache.camel.support.DefaultExchange;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.model.reports.ReportDataRequest;
import org.recap.service.common.SetupDataService;
import org.recap.service.submitcollection.SubmitCollectionBatchService;
import org.recap.service.submitcollection.SubmitCollectionReportGenerator;
import org.recap.service.submitcollection.SubmitCollectionService;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by premkb on 19/3/17.
 */
@Service
@Scope("prototype")
public class SubmitCollectionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SubmitCollectionProcessor.class);

    @Autowired
    private SubmitCollectionService submitCollectionService;

    @Autowired
    private SubmitCollectionBatchService submitCollectionBatchService;

    @Autowired
    private SubmitCollectionReportGenerator submitCollectionReportGenerator;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    PropertyUtil propertyUtil;

    @Value("${email.submit.collection.subject}")
    private String submitCollectionEmailSubject;

    @Value("${email.submit.collection.subject.for.empty.directory}")
    private String subjectForEmptyDirectory;

    private String institutionCode;
    private boolean isCGDProtection;
    private String cgdType;

    @Autowired
    private SetupDataService setupDataService;

    @Autowired
    AmazonS3 awsS3Client;

    public SubmitCollectionProcessor() {
    }

    public SubmitCollectionProcessor(String inputInstitutionCode, boolean isCGDProtection, String cgdType) {
        this.institutionCode = inputInstitutionCode;
        this.isCGDProtection = isCGDProtection;
        this.cgdType=cgdType;
    }

    /**
     * Process input.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    public void processInput(Exchange exchange) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Set<Integer> processedBibIds = new HashSet<>();
        Set<String> updatedBoundWithDummyRecordOwnInstBibIdSet = new HashSet<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        List<Integer> reportRecordNumList = new ArrayList<>();
        String xmlFileName = null;
        String bucketName = null;
        try {
            logger.info("Submit Collection : Route started and started processing the records from s3 for submitcollection");
            String inputXml = exchange.getIn().getBody(String.class);
            xmlFileName = exchange.getIn().getHeader(RecapConstants.CAMEL_AWS_KEY).toString();
            bucketName = exchange.getIn().getHeader("CamelAwsS3BucketName").toString();
            logger.info("Processing xmlFileName----->{}", xmlFileName);
            Integer institutionId = setupDataService.getInstitutionCodeIdMap().get(institutionCode);
            submitCollectionBatchService.process(institutionCode, inputXml, processedBibIds, idMapToRemoveIndexList, bibIdMapToRemoveIndexList, xmlFileName, reportRecordNumList, false, isCGDProtection, updatedBoundWithDummyRecordOwnInstBibIdSet);
            logger.info("Submit Collection : Solr indexing started for {} records", processedBibIds.size());
            logger.info("idMapToRemoveIndex---> {}", idMapToRemoveIndexList.size());
            if (!processedBibIds.isEmpty()) {
                submitCollectionBatchService.indexData(processedBibIds);
                logger.info("Submit Collection : Solr indexing completed and remove the incomplete record from solr index for {} records", idMapToRemoveIndexList.size());
            }
            if (!updatedBoundWithDummyRecordOwnInstBibIdSet.isEmpty()) {
                logger.info("Updated boudwith dummy record own inst bib id size-->{}", updatedBoundWithDummyRecordOwnInstBibIdSet.size());
                submitCollectionService.indexDataUsingOwningInstBibId(new ArrayList<>(updatedBoundWithDummyRecordOwnInstBibIdSet), institutionId);
            }
            if (!idMapToRemoveIndexList.isEmpty() || !bibIdMapToRemoveIndexList.isEmpty()) {//remove the incomplete record from solr index
                StopWatch stopWatchRemovingDummy = new StopWatch();
                stopWatchRemovingDummy.start();
                logger.info("Calling indexing to remove dummy records");
                new Thread(() -> {
                    try {
                        submitCollectionBatchService.removeBibFromSolrIndex(bibIdMapToRemoveIndexList);
                        submitCollectionBatchService.removeSolrIndex(idMapToRemoveIndexList);
                        logger.info("Removed dummy records from solr");
                    } catch (Exception e) {
                        logger.error(RecapCommonConstants.LOG_ERROR, e);
                    }
                }).start();
                stopWatchRemovingDummy.stop();
                logger.info("Time take to call and execute solr call to remove dummy-->{} sec", stopWatchRemovingDummy.getTotalTimeSeconds());
            }
            ReportDataRequest reportRequest = getReportDataRequest(xmlFileName);
            String generatedReportFileName = submitCollectionReportGenerator.generateReport(reportRequest);
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoad(xmlFileName, generatedReportFileName), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION);
            if (awsS3Client.doesObjectExist(bucketName, xmlFileName) && (inputXml != null && !inputXml.equals(""))) {
                String basepath = xmlFileName.substring(0, xmlFileName.lastIndexOf('/'));
                basepath = basepath.substring(0, basepath.lastIndexOf('/'));
                String fileName = xmlFileName.substring(xmlFileName.lastIndexOf('/'));
                awsS3Client.copyObject(bucketName, xmlFileName, bucketName, basepath + "/.done-" + institutionCode + "/cgd_" + cgdType + fileName);
                awsS3Client.deleteObject(bucketName, xmlFileName);
            }
            stopWatch.stop();
            logger.info("Submit Collection : Total time taken for processing through s3---> {} sec", stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
            exchange.setException(e);
        }
    }

    public void caughtException(Exchange exchange) {
        logger.info("inside caught exception..........");
        Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        if (exception != null) {
            String fileName = (String) exchange.getIn().getHeader(Exchange.FILE_NAME);
            String filePath = (String) exchange.getIn().getHeader(Exchange.FILE_PARENT);
            String institutionCode1 = (String) exchange.getIn().getHeader(RecapCommonConstants.INSTITUTION);
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoadForExcepion(institutionCode1, fileName, filePath, exception, exception.getMessage()), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_EXCEPTION);
        }
    }

    private EmailPayLoad getEmailPayLoadForExcepion(String institutionCode, String name, String filePath, Exception exception, String exceptionMessage) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setSubject(RecapConstants.SUBJECT_FOR_SUBMIT_COL_EXCEPTION);
        emailPayLoad.setXmlFileName(name);
        emailPayLoad.setTo(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "email.submit.collection.to"));
        emailPayLoad.setCc(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "email.submit.collection.cc"));
        emailPayLoad.setLocation(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "s3.submit.collection.report.dir"));
        emailPayLoad.setLocation(filePath);
        emailPayLoad.setInstitution(institutionCode.toUpperCase());
        emailPayLoad.setException(exception);
        emailPayLoad.setExceptionMessage(exceptionMessage);
        return emailPayLoad;

    }

    private ReportDataRequest getReportDataRequest(String xmlFileName) {
        ReportDataRequest reportRequest = new ReportDataRequest();
        logger.info("filename--->{}-{}", RecapCommonConstants.SUBMIT_COLLECTION_REPORT, xmlFileName);
        reportRequest.setFileName(RecapCommonConstants.SUBMIT_COLLECTION_REPORT + "-" + xmlFileName);
        reportRequest.setInstitutionCode(institutionCode.toUpperCase());
        reportRequest.setReportType(RecapCommonConstants.SUBMIT_COLLECTION_SUMMARY);
        reportRequest.setTransmissionType(RecapCommonConstants.FTP);
        return reportRequest;
    }

    private EmailPayLoad getEmailPayLoad(String xmlFileName, String reportFileName) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setSubject(submitCollectionEmailSubject);
        emailPayLoad.setReportFileName(reportFileName);
        emailPayLoad.setXmlFileName(xmlFileName);
        emailPayLoad.setTo(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "email.submit.collection.to"));
        emailPayLoad.setCc(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "email.submit.collection.cc"));
        emailPayLoad.setLocation(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "s3.submit.collection.report.dir"));
        emailPayLoad.setInstitution(institutionCode.toUpperCase());
        return emailPayLoad;
    }

    /**
     * This method is used to send email when there are no files in the respective directory
     * @param exchange
     * @throws Exception
     */
    public void sendEmailForEmptyDirectory(Exchange exchange) throws Exception {
        String s3Path = ((S3Endpoint) ((DefaultExchange) exchange).getFromEndpoint()).getConfiguration().getPrefix();
        producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoadForNoFiles(institutionCode,s3Path), RecapConstants.EMAIL_BODY_FOR, RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES);
    }

    private EmailPayLoad getEmailPayLoadForNoFiles(String institutionCode, String ftpLocationPath) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setSubject(subjectForEmptyDirectory+" - "+institutionCode+" - "+cgdType);
        emailPayLoad.setLocation(ftpLocationPath);
        emailPayLoad.setTo(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "email.submit.collection.nofiles.to"));
        return  emailPayLoad;
    }
}
