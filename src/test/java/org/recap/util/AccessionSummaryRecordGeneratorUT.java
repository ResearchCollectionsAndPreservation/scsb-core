package org.recap.util;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.AccessionSummaryRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Created by Anitha V on 10/10/20.
 */

public class AccessionSummaryRecordGeneratorUT extends BaseTestCaseUT {

    @InjectMocks
    AccessionSummaryRecordGenerator accessionSummaryRecordGenerator;

    @Test
    public void successBibCount()throws Exception {
        ReportEntity reportEntity=new ReportEntity();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        reportDataEntityList.add(getReportDataEntity(RecapCommonConstants.BIB_SUCCESS_COUNT));
        reportEntity.setReportDataEntities(reportDataEntityList);
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        List<AccessionSummaryRecord> accessionSummaryRecordList=accessionSummaryRecordGenerator.prepareAccessionSummaryReportRecord(reportEntityList);
        assertEquals("1",accessionSummaryRecordList.get(0).getSuccessBibCount());
    }

    @Test
    public void successItemCount()throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        reportDataEntityList.add(getReportDataEntity(RecapCommonConstants.ITEM_SUCCESS_COUNT));
        reportEntity.setReportDataEntities(reportDataEntityList);
        reportEntityList.add(reportEntity);
        List<AccessionSummaryRecord> accessionSummaryRecordList=accessionSummaryRecordGenerator.prepareAccessionSummaryReportRecord(reportEntityList);
        assertEquals("1",accessionSummaryRecordList.get(0).getSuccessItemCount());
    }

    @Test
    public void failedBibCount()throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        reportDataEntityList.add(getReportDataEntity(RecapCommonConstants.BIB_FAILURE_COUNT));
        reportDataEntityList.add(getReportDataEntity(RecapConstants.FAILURE_BIB_REASON));
        reportEntity.setReportDataEntities(reportDataEntityList);
        reportEntityList.add(reportEntity);
        List<AccessionSummaryRecord> accessionSummaryRecordList=accessionSummaryRecordGenerator.prepareAccessionSummaryReportRecord(reportEntityList);
        assertEquals("1",accessionSummaryRecordList.get(0).getFailedBibCount());
    }
    @Test
    public void failedItemCount()throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        reportDataEntityList.add(getReportDataEntity(RecapCommonConstants.ITEM_FAILURE_COUNT));
        reportDataEntityList.add(getReportDataEntity(RecapConstants.FAILURE_ITEM_REASON));
        reportEntity.setReportDataEntities(reportDataEntityList);
        reportEntityList.add(reportEntity);
        List<AccessionSummaryRecord> accessionSummaryRecordList=accessionSummaryRecordGenerator.prepareAccessionSummaryReportRecord(reportEntityList);
        assertEquals("1",accessionSummaryRecordList.get(0).getFailedItemCount());
    }

    @Test
    public void noOfBibMatches()throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        reportDataEntityList.add(getReportDataEntity(RecapCommonConstants.NUMBER_OF_BIB_MATCHES));
        reportEntity.setReportDataEntities(reportDataEntityList);
        reportEntityList.add(reportEntity);
        List<AccessionSummaryRecord> accessionSummaryRecordList=accessionSummaryRecordGenerator.prepareAccessionSummaryReportRecord(reportEntityList);
        assertEquals("1",accessionSummaryRecordList.get(0).getNoOfBibMatches());
    }

    private ReportDataEntity getReportDataEntity(String name) {
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntity.setHeaderName(name);
        reportDataEntity.setHeaderValue("1");
        return reportDataEntity;
    }

}
