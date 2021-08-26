package org.recap.service.statusreconciliation;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.TestUtil;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.gfa.ScsbLasItemStatusCheckModel;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.SolrDocIndexService;
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
    StatusReconciliationService mockStatusReconciliationService;

    @Mock
    CommonUtil commonUtil;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Value("${" + PropertyKeyConstants.SCSB_CIRC_URL + "}")
    String scsbCircUrl;

    @Mock
    ScsbLasItemStatusCheckModel mockScsbLasItemStatusCheckModel;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    RequestStatusEntity requestStatusEntity;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    ItemStatusEntity itemStatusEntity;

    @Mock
    RequestItemEntity requestItemEntity;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    SolrDocIndexService solrDocIndexService;

    @Mock
    RequestStatusEntity byRequestStatusCode;


    @Test
    public void itemStatusComparisonCancelRequest() throws Exception {
        List<List<ItemEntity>> itemEntityChunkList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setImsLocationEntity(TestUtil.getImsLocationEntity(3,"RECAP","RECAP_LAS"));
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(itemEntity);
        itemEntityChunkList.add(itemEntityList);
        List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecordList=new ArrayList<>();
        Mockito.when(commonUtil.getScsbItemStatusModelListByItemEntities(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(commonUtil.getBarcodesList(Mockito.anyList())).thenThrow(NullPointerException.class);
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        Mockito.when(mockScsbLasItemStatusCheckModel.getItemBarcode()).thenReturn("123456");
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"getGFAItemStatusCheckResponse",gfaItemStatusCheckResponseItems);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList, 0)).thenCallRealMethod();
        List<ItemChangeLogEntity> itemChangeLogEntityList=new ArrayList<>();
        List<String> requestStatusCodes = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.getRequestItemEntitiesBasedOnDayLimit(Mockito.anyInt(),Mockito.anyList(),Mockito.anyInt())).thenReturn(Arrays.asList(1,2,3));
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCodeIn(requestStatusCodes)).thenReturn(requestStatusEntityList);
        Mockito.when(itemStatusDetailsRepository.findById(Mockito.anyInt())).thenReturn(java.util.Optional.of(itemStatusEntity));
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        Mockito.when(requestItemDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(requestItemEntityList);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(requestStatusEntity.getRequestStatusCode()).thenReturn(ScsbCommonConstants.REQUEST_STATUS_CANCELED);
        Mockito.when(requestItemEntity.getNotes()).thenReturn("Cancel requested");
        Mockito.when(requestItemEntity.getId()).thenReturn(1);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0);
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"processMismatchStatus",statusReconciliationCSVRecordList,itemChangeLogEntityList,"IN",itemEntity);
        assertNotNull(statusReconciliationCSVRecordList);
    }

    @Test
    public void itemStatusComparison() throws Exception {
        List<List<ItemEntity>> itemEntityChunkList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setImsLocationEntity(TestUtil.getImsLocationEntity(3,"RECAP","RECAP_LAS"));
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(itemEntity);
        itemEntityChunkList.add(itemEntityList);
        List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecordList=new ArrayList<>();
        Mockito.when(commonUtil.getScsbItemStatusModelListByItemEntities(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(commonUtil.getBarcodesList(Mockito.anyList())).thenThrow(NullPointerException.class);
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        Mockito.when(mockScsbLasItemStatusCheckModel.getItemBarcode()).thenReturn("123456");
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"getGFAItemStatusCheckResponse",gfaItemStatusCheckResponseItems);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0)).thenCallRealMethod();
        List<ItemChangeLogEntity> itemChangeLogEntityList=new ArrayList<>();
        List<String> requestStatusCodes = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.getRequestItemEntitiesBasedOnDayLimit(Mockito.anyInt(),Mockito.anyList(),Mockito.anyInt())).thenReturn(Arrays.asList(1,2,3));
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCodeIn(requestStatusCodes)).thenReturn(requestStatusEntityList);
        Mockito.when(itemStatusDetailsRepository.findById(Mockito.anyInt())).thenReturn(java.util.Optional.of(itemStatusEntity));
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        Mockito.when(requestItemDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(requestItemEntityList);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(requestStatusEntity.getRequestStatusCode()).thenReturn("test");
        Mockito.when(requestItemEntity.getNotes()).thenReturn("Cancel requested");
        Mockito.when(requestItemEntity.getId()).thenReturn(1);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0);
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"processMismatchStatus",statusReconciliationCSVRecordList,itemChangeLogEntityList,"IN",itemEntity);
        assertNotNull(statusReconciliationCSVRecordList);
    }

    @Test
    public void itemStatusComparisonSolrCancelled() throws Exception {
        List<List<ItemEntity>> itemEntityChunkList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setImsLocationEntity(TestUtil.getImsLocationEntity(3,"RECAP","RECAP_LAS"));
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(itemEntity);
        itemEntityChunkList.add(itemEntityList);
        List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecordList=new ArrayList<>();
        Mockito.when(commonUtil.getScsbItemStatusModelListByItemEntities(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(commonUtil.getBarcodesList(Mockito.anyList())).thenThrow(NullPointerException.class);
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        Mockito.when(mockScsbLasItemStatusCheckModel.getItemBarcode()).thenReturn("123456");
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"getGFAItemStatusCheckResponse",gfaItemStatusCheckResponseItems);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemDetailsRepository",itemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"solrDocIndexService",solrDocIndexService);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0)).thenCallRealMethod();
        List<ItemChangeLogEntity> itemChangeLogEntityList=new ArrayList<>();
        List<String> requestStatusCodes = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.getRequestItemEntitiesBasedOnDayLimit(Mockito.anyInt(),Mockito.anyList(),Mockito.anyInt())).thenReturn(Arrays.asList(1,2,3));
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCodeIn(requestStatusCodes)).thenReturn(requestStatusEntityList);
        Mockito.when(itemStatusDetailsRepository.findById(Mockito.anyInt())).thenReturn(java.util.Optional.of(itemStatusEntity));
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        Mockito.when(requestItemDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(requestItemEntityList);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(requestStatusEntity.getRequestStatusCode()).thenReturn("test");
        Mockito.when(requestItemEntity.getNotes()).thenReturn("Cancel requested");
        Mockito.when(requestItemEntity.getId()).thenReturn(1);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0);
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"processMismatchStatus",statusReconciliationCSVRecordList,itemChangeLogEntityList,"IN",itemEntity);
        assertNotNull(statusReconciliationCSVRecordList);
    }

    @Test
    public void itemStatusComparisonSolr() throws Exception {
        List<List<ItemEntity>> itemEntityChunkList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setImsLocationEntity(TestUtil.getImsLocationEntity(3,"RECAP","RECAP_LAS"));
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(itemEntity);
        itemEntityChunkList.add(itemEntityList);
        List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecordList=new ArrayList<>();
        Mockito.when(commonUtil.getScsbItemStatusModelListByItemEntities(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(commonUtil.getBarcodesList(Mockito.anyList())).thenThrow(NullPointerException.class);
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        Mockito.when(mockScsbLasItemStatusCheckModel.getItemBarcode()).thenReturn("123456");
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"getGFAItemStatusCheckResponse",gfaItemStatusCheckResponseItems);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0)).thenCallRealMethod();
        List<ItemChangeLogEntity> itemChangeLogEntityList=new ArrayList<>();
        List<String> requestStatusCodes = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.getRequestItemEntitiesBasedOnDayLimit(Mockito.anyInt(),Mockito.anyList(),Mockito.anyInt())).thenReturn(Arrays.asList(1,2,3));
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCodeIn(requestStatusCodes)).thenReturn(requestStatusEntityList);
        Mockito.when(itemStatusDetailsRepository.findById(Mockito.anyInt())).thenReturn(java.util.Optional.of(itemStatusEntity));
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        Mockito.when(requestItemDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(requestItemEntityList);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(requestStatusEntity.getRequestStatusCode()).thenReturn(ScsbCommonConstants.REQUEST_STATUS_CANCELED);
        Mockito.when(requestItemEntity.getNotes()).thenReturn("test");
        Mockito.when(requestItemEntity.getId()).thenReturn(1);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(byRequestStatusCode);
        Mockito.when(byRequestStatusCode.getId()).thenReturn(1);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList, 0);
        ReflectionTestUtils.invokeMethod(mockStatusReconciliationService,"processMismatchStatus",statusReconciliationCSVRecordList,itemChangeLogEntityList,"IN",itemEntity);
        assertNotNull(statusReconciliationCSVRecordList);
    }

    @Test
    public void getStatusReconciliationCSVRecord() throws Exception {
        ItemStatusEntity itemStatusEntity=new ItemStatusEntity();
        statusReconciliationService.reFileItems(Arrays.asList("123456"),Arrays.asList(1));
        StatusReconciliationCSVRecord statusReconciliationCSVRecord=statusReconciliationService.getStatusReconciliationCSVRecord("12345", "PUL", "PUL", "Available","1","IN",new Date().toString(),new Date().toString(),itemStatusEntity, "RECAP", false, true,false);
        assertEquals("12345",statusReconciliationCSVRecord.getBarcode());
    }

}
