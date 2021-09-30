package org.recap.service.statusreconciliation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.TestUtil;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.gfa.ScsbLasItemStatusCheckModel;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.service.SolrDocIndexService;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource("classpath:application.properties")
public class StatusReconciliationServiceUT {

    @InjectMocks
    @Spy
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
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbCircUrl","scsbCircUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbUrl","scsbUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList, 0)).thenCallRealMethod();
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0);
        assertNotNull(statusReconciliationCSVRecordList);
    }

    @Test
    public void processMismatchStatus(){
        RequestStatusEntity requestStatusEntity = getRequestStatusEntity();
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList = new ArrayList<>();
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        List<RequestStatusEntity> requestStatusEntityList = new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        String lasStatus = "IN";
        ItemEntity itemEntity = getItemEntity();
        boolean isUnknownCode = false;
        boolean refileRequired = true;
        int refileCount = 1;
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCodeIn(any())).thenReturn(requestStatusEntityList);
        Mockito.when(requestItemDetailsRepository.getRequestItemEntitiesBasedOnDayLimit(any(), any(), any())).thenReturn(Arrays.asList(1));
        Mockito.when(requestItemDetailsRepository.findByIdIn(any())).thenReturn(Arrays.asList(getRequestItemEntity()));
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(getRequestStatusEntity());
        ReflectionTestUtils.setField(statusReconciliationService,"statusReconciliationRefileMaxCapLimit",10);
        ReflectionTestUtils.invokeMethod(statusReconciliationService,"processMismatchStatus",statusReconciliationCSVRecordList,itemChangeLogEntityList,lasStatus,itemEntity,isUnknownCode,refileRequired,refileCount);
    }
    @Test
    public void processMismatchStatusException(){
        RequestStatusEntity requestStatusEntity = getRequestStatusEntity();
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList = new ArrayList<>();
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        List<RequestStatusEntity> requestStatusEntityList = new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        String lasStatus = "IN";
        ItemEntity itemEntity = getItemEntity();
        boolean isUnknownCode = false;
        boolean refileRequired = true;
        int refileCount = 1;
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCodeIn(any())).thenReturn(requestStatusEntityList);
        Mockito.when(requestItemDetailsRepository.getRequestItemEntitiesBasedOnDayLimit(any(), any(), any())).thenReturn(Arrays.asList(1));
        Mockito.when(requestItemDetailsRepository.findByIdIn(any())).thenReturn(Collections.EMPTY_LIST);
        ReflectionTestUtils.setField(statusReconciliationService,"statusReconciliationRefileMaxCapLimit",10);
        ReflectionTestUtils.invokeMethod(statusReconciliationService,"processMismatchStatus",statusReconciliationCSVRecordList,itemChangeLogEntityList,lasStatus,itemEntity,isUnknownCode,refileRequired,refileCount);
    }

    private RequestItemEntity getRequestItemEntity() {
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        requestItemEntity.setRequestStatusEntity(getRequestStatusEntity());
        return requestItemEntity;
    }

    private RequestStatusEntity getRequestStatusEntity() {
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setId(1);
        requestStatusEntity.setRequestStatusCode("CANCELED");
        requestStatusEntity.setRequestStatusDescription("CANCELED");
        return requestStatusEntity;
    }

    @Test
    public void getStatusReconciliationCSVRecordIsUnknownCode() throws Exception {
        Mockito.when(mockStatusReconciliationService.getStatusReconciliationCSVRecord("123456",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.PRINCETON,"IN","1","IN","updatedDateTime","requestedDateTime",itemStatusEntity,"RECAP",true,true,false)).thenCallRealMethod();
        StatusReconciliationCSVRecord statusReconciliationCSVRecord =mockStatusReconciliationService.getStatusReconciliationCSVRecord("123456",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.PRINCETON,"IN","1","IN","updatedDateTime","requestedDateTime",itemStatusEntity,"RECAP",true,true,false);
        assertNotNull(statusReconciliationCSVRecord);
    }

    @Test
    public void getStatusReconciliationCSVRecordIsRefileCapNotExceeded() throws Exception {
        Mockito.when(mockStatusReconciliationService.getStatusReconciliationCSVRecord("123456",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.PRINCETON,"IN","1","IN","updatedDateTime","requestedDateTime",itemStatusEntity,"RECAP",false,true,true)).thenCallRealMethod();
        StatusReconciliationCSVRecord statusReconciliationCSVRecord =mockStatusReconciliationService.getStatusReconciliationCSVRecord("123456",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.PRINCETON,"IN","1","IN","updatedDateTime","requestedDateTime",itemStatusEntity,"RECAP",false,true,true);
        assertNotNull(statusReconciliationCSVRecord);
    }

    @Test
    public void getStatusReconciliationCSVRecord() throws Exception {
        Mockito.when(mockStatusReconciliationService.getStatusReconciliationCSVRecord("123456",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.PRINCETON,"IN","1","IN","updatedDateTime","requestedDateTime",itemStatusEntity,"RECAP",false,false,false)).thenCallRealMethod();
        StatusReconciliationCSVRecord statusReconciliationCSVRecord =mockStatusReconciliationService.getStatusReconciliationCSVRecord("123456",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.PRINCETON,"IN","1","IN","updatedDateTime","requestedDateTime",itemStatusEntity,"RECAP",false,false,false);
        assertNotNull(statusReconciliationCSVRecord);
    }

    @Test
    public void statusReconciliationCSVRecord(){
        String lasStatus = "IN";
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        requestItemEntity.setInstitutionEntity(itemEntity.getInstitutionEntity());
        requestItemEntity.setLastUpdatedDate(new Date());
        List<String> barcodeList = new ArrayList<>();
        List<Integer> requestIdList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        boolean isUnknownCode = false;
        boolean refileRequired = true;
        boolean isRefileCapNotExceeded = true;
        StatusReconciliationCSVRecord statusReconciliationCSVRecord = new StatusReconciliationCSVRecord();
        Mockito.when(statusReconciliationService.getStatusReconciliationCSVRecord(any(), any(), any(), any(), any(), any(),any(), any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(statusReconciliationCSVRecord);
        ReflectionTestUtils.invokeMethod(statusReconciliationService,"getStatusReconciliationCSVRecord",lasStatus,itemEntity,barcodeList,requestIdList,simpleDateFormat,itemStatusEntity,requestItemEntity,isUnknownCode,refileRequired,isRefileCapNotExceeded);
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
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbCircUrl","scsbCircUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbUrl","scsbUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0)).thenCallRealMethod();
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0);
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
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbCircUrl","scsbCircUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbUrl","scsbUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemDetailsRepository",itemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"solrDocIndexService",solrDocIndexService);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0)).thenCallRealMethod();
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0);
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
        List<ScsbLasItemStatusCheckModel> gfaItemStatusCheckResponseItems=new ArrayList<>();
        gfaItemStatusCheckResponseItems.add(mockScsbLasItemStatusCheckModel);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbCircUrl","scsbCircUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"scsbUrl","scsbUrl");
        ReflectionTestUtils.setField(mockStatusReconciliationService,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemStatusDetailsRepository",requestItemStatusDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"requestItemDetailsRepository",requestItemDetailsRepository);
        ReflectionTestUtils.setField(mockStatusReconciliationService,"itemStatusDetailsRepository",itemStatusDetailsRepository);
        Mockito.when(mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList,0)).thenCallRealMethod();
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList=mockStatusReconciliationService.itemStatusComparison(itemEntityChunkList,statusReconciliationErrorCSVRecordList, 0);
        assertNotNull(statusReconciliationCSVRecordList);
    }

    @Test
    public void saveItemChangeLogEntity(){
        Integer requestId = 1;
        String barcode = "46789";
        ReflectionTestUtils.invokeMethod(statusReconciliationService,"saveItemChangeLogEntity",requestId,barcode);
    }

    @Test
    public void getHttpHeadersAuth(){
        try {
            ReflectionTestUtils.invokeMethod(statusReconciliationService,"getHttpHeadersAuth");
        }catch (Exception e){}
    }

    @Test
    public void reFileItems(){
        List<String> itemBarcodes = new ArrayList<>();
        List<Integer> requestIdList = new ArrayList<>();
        statusReconciliationService.reFileItems(itemBarcodes,requestIdList);
    }
    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("HD");
        itemEntity.setBarcode("567889");
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setImsLocationEntity(imsLocationEntity);
        return itemEntity;
    }

}
