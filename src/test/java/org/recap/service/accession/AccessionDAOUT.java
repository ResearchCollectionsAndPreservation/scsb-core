package org.recap.service.accession;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 7/7/17.
 */
public class AccessionDAOUT extends BaseTestCaseUT {

    @InjectMocks
    AccessionDAO accessionDAO;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    EntityManager entityManager;

    @Test
    public void testAccessionDAO(){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("Accession");
        reportEntity.setType("Success");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName("BibId");
        reportDataEntity.setHeaderValue("123");
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        Mockito.when(reportDetailRepository.save(Mockito.any())).thenReturn(reportEntity);
        ReportEntity savedReportEntity = accessionDAO.saveReportEntity(reportEntity);
        assertNotNull(savedReportEntity);
    }

    @Test
    public void saveBibRecord(){
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(Mockito.any())).thenReturn(bibliographicEntity);
        Mockito.doNothing().when(entityManager).refresh(Mockito.any());
        BibliographicEntity savedBibliographicEntity = accessionDAO.saveBibRecord(bibliographicEntity);
    }
}