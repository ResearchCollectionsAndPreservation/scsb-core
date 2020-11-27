package org.recap.controller;

import org.apache.camel.CamelContext;
import org.recap.RecapCommonConstants;
import org.recap.camel.accessionreconciliation.BarcodeReconciliationRouteBuilder;
import org.recap.model.ILSConfigProperties;
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
@RequestMapping("/accessionReconcilation")
public class AccessionReconcilationJobController {

    private static final Logger logger = LoggerFactory.getLogger(AccessionReconcilationJobController.class);

    @Autowired
    PropertyUtil propertyUtil;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    CamelContext camelContext;

    /**
     * This method is used for generating report by, comparing LAS(ReCAP) barcodes and SCSB barcodes. The LAS barcodes are send to SCSB as CVS files, in specific FTP folder.
     * The barcodes are physically seprated by institution. This method will initiate the comparison of all the three institution at the same time.
     *
     * @return String
     * @throws Exception
     */
    @PostMapping(value = "/startAccessionReconcilation")
    public String startAccessionReconcilation() throws Exception {
        logger.info("Before accession reconciliation process : {}", camelContext.getRoutes().size());
        logger.info("Starting Accession Reconcilation Routes");
        List<String> allInstitutionCodeExceptHTC = institutionDetailsRepository.findAllInstitutionCodeExceptHTC();
        for (String institution : allInstitutionCodeExceptHTC) {
            ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(institution);
            camelContext.addRoutes(new BarcodeReconciliationRouteBuilder(applicationContext, camelContext,
                    institution, ilsConfigProperties.getFtpAccessionReconciliationDir(),
                    ilsConfigProperties.getAccessionReconciliationFilepath(), ilsConfigProperties.getFtpAccessionReconciliationProcessedDir()));
        }
        for (String institution : allInstitutionCodeExceptHTC) {
            camelContext.getRouteController().startRoute(institution + "accessionReconcilationFtpRoute");
        }
        logger.info("After accession reconciliation process : {}", camelContext.getRoutes().size());
        return RecapCommonConstants.SUCCESS;
    }
}
