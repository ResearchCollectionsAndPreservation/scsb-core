package org.recap.service.submitcollection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.model.reports.ReportDataRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 23/3/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionReportGeneratorUT{

    @Mock
    private SubmitCollectionReportGenerator submitCollectionReportGenerator;

    @Value("${scsb.solr.client.url}")
    private String solrClientUrl;

    @Mock
    RestTemplate restTemplate;

    public String getSolrClientUrl() {
        return solrClientUrl;
    }
@Before
public void setup(){
    Mockito.when(submitCollectionReportGenerator.getSolrClientUrl()).thenReturn(solrClientUrl);
    Mockito.when(submitCollectionReportGenerator.getRestTemplate()).thenReturn(restTemplate);
}
    @Test
    public void generateReport(){
        ReportDataRequest reportDataRequest = new ReportDataRequest();
        reportDataRequest.setFileName("Submit_Collection_Report");
        reportDataRequest.setInstitutionCode("PUL");
        reportDataRequest.setReportType("Submit_Collection_Exception_Report");
        reportDataRequest.setTransmissionType("FTP");
        Mockito.when(submitCollectionReportGenerator.getSolrClientUrl()).thenReturn(solrClientUrl);
        Mockito.when(submitCollectionReportGenerator.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(submitCollectionReportGenerator.getRestTemplate().postForObject(getSolrClientUrl() + "/reportsService/generateCsvReport", reportDataRequest, String.class)).thenReturn("Submit_Collection_Report");
        Mockito.when(submitCollectionReportGenerator.generateReport(reportDataRequest)).thenCallRealMethod();
        String response = submitCollectionReportGenerator.generateReport(reportDataRequest);
        assertNotNull(response);
    }

    @Test
    public void testReportDataRequest(){
        ReportDataRequest reportDataRequest = new ReportDataRequest();
        reportDataRequest.setFileName("Submit_Collection_Report");
        reportDataRequest.setInstitutionCode("PUL");
        reportDataRequest.setReportType("Submit_Collection_Exception_Report");
        reportDataRequest.setTransmissionType("FTP");

        assertNotNull(reportDataRequest.getFileName());
        assertNotNull(reportDataRequest.getInstitutionCode());
        assertNotNull(reportDataRequest.getReportType());
        assertNotNull(reportDataRequest.getTransmissionType());
    }
}
