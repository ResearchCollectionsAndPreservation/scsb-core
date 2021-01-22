package org.recap.controller;

import org.apache.camel.CamelContext;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.accessionreconciliation.BarcodeReconciliationRouteBuilder;
import org.recap.repository.jpa.ImsLocationDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by akulak on 24/5/17.
 */
@RestController
@RequestMapping("/accessionReconciliation")
public class AccessionReconcilationJobController {

    private static final Logger logger = LoggerFactory.getLogger(AccessionReconcilationJobController.class);

    @Autowired
    PropertyUtil propertyUtil;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    CamelContext camelContext;

    @Value("${s3.accession.reconciliation.dir}")
    private String accessionReconciliationPath;

    @Value("${accession.reconciliation.filePath}")
    private String accessionReconciliationFilePath;

    @Value("${s3.accession.reconciliation.processed.dir}")
    private String accessionReconciliationProcessedPath;

    /**
     * This method is used for generating report by, comparing each LAS(ReCAP/HD) barcodes and SCSB barcodes. The LAS barcodes are send to SCSB as CVS files, in specific FTP folder.
     * The barcodes are physically separated by institution. This method will initiate the comparison of all the three institution at the same time.
     *
     * @return String
     * @throws Exception
     */
    @PostMapping(value = "/startAccessionReconciliation")
    public String startAccessionReconciliation() throws Exception {
        logger.info("Before accession reconciliation process : {}", camelContext.getRoutes().size());
        logger.info("Starting Accession Reconciliation Routes");
        List<String> imsLocationCodesExceptUN = imsLocationDetailsRepository.findAllImsLocationCodeExceptUN();
        List<String> allInstitutionCodeExceptHTC = institutionDetailsRepository.findAllInstitutionCodeExceptHTC();
        for (String imsLocation : imsLocationCodesExceptUN) {
            for (String institution : allInstitutionCodeExceptHTC) {
                camelContext.addRoutes(new BarcodeReconciliationRouteBuilder(applicationContext, camelContext,
                        institution, imsLocation, accessionReconciliationPath, accessionReconciliationFilePath, accessionReconciliationProcessedPath));
            }
        }
        for (String imsLocation : imsLocationCodesExceptUN) {
            for (String institution : allInstitutionCodeExceptHTC) {
                camelContext.getRouteController().startRoute(imsLocation + institution + RecapConstants.ACCESSION_RECONCILIATION_S3_ROUTE_ID);
            }
        }
        logger.info("After accession reconciliation process : {}", camelContext.getRoutes().size());
        return RecapCommonConstants.SUCCESS;
    }
}
