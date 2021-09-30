package org.recap.service.submitcollection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.model.reports.ReportDataRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 23/3/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionReportGeneratorUT {

    @InjectMocks
    @Spy
    private SubmitCollectionReportGenerator submitCollectionReportGenerator;

    @Value("${" + PropertyKeyConstants.SCSB_SOLR_DOC_URL + "}")
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
        try {
            String response = submitCollectionReportGenerator.generateReport(reportDataRequest);
            assertNotNull(response);
        }catch (Exception e){}

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
