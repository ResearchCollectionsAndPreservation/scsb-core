package org.recap.service.accession.callable;

import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.util.AccessionProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by sheiks on 26/05/17.
 */
@Component
@Scope("prototype")
public class BibDataCallable implements Callable{
    private static final Logger logger = LoggerFactory.getLogger(BibDataCallable.class);

    @Autowired
    private AccessionProcessService accessionProcessService;
    private AccessionRequest accessionRequest;
    private String owningInstitution;
    private boolean writeToReport;

    @Override
    public Object call() throws Exception {

        List<Map<String, String>> responseMaps = new ArrayList<>();
        List<ReportDataEntity> reportDataEntitys = new ArrayList<>();
        Set<AccessionResponse> accessionResponses = new HashSet<>();

        return accessionProcessService.processRecords(accessionResponses, responseMaps, accessionRequest, reportDataEntitys, owningInstitution, writeToReport);

    }
    public void setAccessionRequest(AccessionRequest accessionRequest) {
        this.accessionRequest = accessionRequest;
    }


    public void setWriteToReport(boolean writeToReport) {
        this.writeToReport = writeToReport;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }
}
