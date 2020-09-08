package org.recap.util;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.AccessionSummaryRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hemalathas on 22/11/16.
 */
public class AccessionSummaryRecordGenerator {

    /**
     * This method is used to prepare accession summary report.
     *
     * @param reportEntityList the report entity list
     * @return the list
     */
    public List<AccessionSummaryRecord> prepareAccessionSummaryReportRecord(List<ReportEntity> reportEntityList){
        Integer bibSuccessCount = 0;
        Integer itemSuccessCount = 0;
        Integer bibFailureCount = 0;
        Integer itemFailureCount = 0;
        Integer existingBibCount = 0;
        List<AccessionSummaryRecord> accessionSummaryRecordList = new ArrayList<>();
        Map<String,Integer> bibFailureReasonCountMap = new HashMap<>();
        Map<String,Integer> itemFailureReasonCountMap = new HashMap<>();

        for(ReportEntity reportEntity : reportEntityList){
            for(ReportDataEntity reportDataEntity : reportEntity.getReportDataEntities()){
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapCommonConstants.BIB_SUCCESS_COUNT)){
                    bibSuccessCount = bibSuccessCount + Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapCommonConstants.ITEM_SUCCESS_COUNT)){
                    itemSuccessCount = itemSuccessCount + Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapCommonConstants.BIB_FAILURE_COUNT)){
                    bibFailureCount = Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapCommonConstants.ITEM_FAILURE_COUNT)){
                    itemFailureCount = Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapCommonConstants.NUMBER_OF_BIB_MATCHES)){
                    existingBibCount = existingBibCount + Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                addToDocFailureReasonCountMap(bibFailureCount, bibFailureReasonCountMap, reportDataEntity, RecapConstants.FAILURE_BIB_REASON);
                addToDocFailureReasonCountMap(itemFailureCount, itemFailureReasonCountMap, reportDataEntity, RecapConstants.FAILURE_ITEM_REASON);
            }
        }

        AccessionSummaryRecord accessionSummaryRecord = new AccessionSummaryRecord();
        accessionSummaryRecord.setSuccessBibCount(bibSuccessCount.toString());
        accessionSummaryRecord.setSuccessItemCount(itemSuccessCount.toString());
        accessionSummaryRecord.setNoOfBibMatches(existingBibCount.toString());
        if(bibFailureReasonCountMap.size() != 0){
            removeFromBibFailureReasonCountMap(bibFailureReasonCountMap, accessionSummaryRecord);
        }
        if(itemFailureReasonCountMap.size() != 0){
            removeFromItemFailureReasonCountMap(itemFailureReasonCountMap, accessionSummaryRecord);
        }
        accessionSummaryRecordList.add(accessionSummaryRecord);

        if(itemFailureReasonCountMap.size() != 0 && bibFailureReasonCountMap.size() <= itemFailureReasonCountMap.size()){
            int count =0;
            while (count < bibFailureReasonCountMap.size()){
                buildAccessionSummaryRecordList(accessionSummaryRecordList, bibFailureReasonCountMap, itemFailureReasonCountMap);
                count+=1;
            }
            if(itemFailureReasonCountMap.size() != 0){
                for(String key : itemFailureReasonCountMap.keySet()){
                    AccessionSummaryRecord accessionSummaryRec = new AccessionSummaryRecord();
                    accessionSummaryRec.setReasonForFailureItem(key);
                    accessionSummaryRec.setFailedItemCount(itemFailureReasonCountMap.get(key).toString());
                    accessionSummaryRecordList.add(accessionSummaryRec);
                }
            }
        }else if(bibFailureReasonCountMap.size() != 0 && bibFailureReasonCountMap.size() > itemFailureReasonCountMap.size()){
            int count =0;
            while (count < itemFailureReasonCountMap.size()){
                buildAccessionSummaryRecordList(accessionSummaryRecordList, bibFailureReasonCountMap, itemFailureReasonCountMap);
                count+=1;
            }
            if(bibFailureReasonCountMap.size() != 0){
                for(String key : bibFailureReasonCountMap.keySet()){
                    AccessionSummaryRecord accessionSummaryRec = new AccessionSummaryRecord();
                    accessionSummaryRec.setReasonForFailureBib(key);
                    accessionSummaryRec.setFailedBibCount(bibFailureReasonCountMap.get(key).toString());
                    accessionSummaryRecordList.add(accessionSummaryRec);
                }
            }
        }
        return accessionSummaryRecordList;
    }

    private void addToDocFailureReasonCountMap(Integer bibFailureCount, Map<String, Integer> failureReasonCountMap, ReportDataEntity reportDataEntity, String failureBibReason) {
        if (reportDataEntity.getHeaderName().equalsIgnoreCase(failureBibReason) && !StringUtils.isEmpty(reportDataEntity.getHeaderValue())) {
            Integer bibCount = failureReasonCountMap.get(reportDataEntity.getHeaderValue());
            if (bibCount != null) {
                failureReasonCountMap.put(reportDataEntity.getHeaderValue(), bibCount + bibFailureCount);
            } else {
                failureReasonCountMap.put(reportDataEntity.getHeaderValue(), bibFailureCount);
            }
        }
    }

    private void buildAccessionSummaryRecordList(List<AccessionSummaryRecord> accessionSummaryRecordList, Map<String, Integer> bibFailureReasonCountMap, Map<String, Integer> itemFailureReasonCountMap) {
        AccessionSummaryRecord accessionSummaryRec = new AccessionSummaryRecord();
        removeFromBibFailureReasonCountMap(bibFailureReasonCountMap, accessionSummaryRec);
        removeFromItemFailureReasonCountMap(itemFailureReasonCountMap, accessionSummaryRec);
        accessionSummaryRecordList.add(accessionSummaryRec);
    }

    private void removeFromBibFailureReasonCountMap(Map<String, Integer> bibFailureReasonCountMap, AccessionSummaryRecord accessionSummaryRecord) {
        Map.Entry<String, Integer> bibEntry = bibFailureReasonCountMap.entrySet().iterator().next();
        accessionSummaryRecord.setReasonForFailureBib(bibEntry.getKey());
        accessionSummaryRecord.setFailedBibCount(bibEntry.getValue().toString());
        bibFailureReasonCountMap.remove(bibEntry.getKey());
    }

    private void removeFromItemFailureReasonCountMap(Map<String, Integer> itemFailureReasonCountMap, AccessionSummaryRecord accessionSummaryRecord) {
        Map.Entry<String, Integer> itemEntry = itemFailureReasonCountMap.entrySet().iterator().next();
        accessionSummaryRecord.setReasonForFailureItem(itemEntry.getKey());
        accessionSummaryRecord.setFailedItemCount(itemEntry.getValue().toString());
        itemFailureReasonCountMap.remove(itemEntry.getKey());
    }

}
