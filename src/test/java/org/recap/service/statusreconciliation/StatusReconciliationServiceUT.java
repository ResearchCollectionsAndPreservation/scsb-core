package org.recap.service.statusreconciliation;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatusReconciliationServiceUT extends BaseTestCaseUT {

    @InjectMocks
    StatusReconciliationService statusReconciliationService;

    @Mock
    CommonUtil commonUtil;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Test
    public void itemStatusComparison() throws Exception {
        List<List<ItemEntity>> itemEntityChunkList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntityChunkList.add(Arrays.asList(itemEntity));
        List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecordList=new ArrayList<>();
        Mockito.when(commonUtil.getBarcodesList(Mockito.anyList())).thenThrow(NullPointerException.class);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=statusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList);
        assertNotNull(statusReconciliationCSVRecordList);
    }

    @Test
    public void getStatusReconciliationCSVRecord() throws Exception {
        ItemStatusEntity itemStatusEntity=new ItemStatusEntity();
        statusReconciliationService.reFileItems(Arrays.asList("123456"),Arrays.asList(1));
        StatusReconciliationCSVRecord statusReconciliationCSVRecord=statusReconciliationService.getStatusReconciliationCSVRecord("12345","Available","1","IN",new Date().toString(),itemStatusEntity, "RECAP");
        assertEquals("12345",statusReconciliationCSVRecord.getBarcode());
    }

}
