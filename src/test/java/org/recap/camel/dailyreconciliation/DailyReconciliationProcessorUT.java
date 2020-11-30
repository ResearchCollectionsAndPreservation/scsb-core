package org.recap.camel.dailyreconciliation;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.RouteController;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.csv.DailyReconcilationRecord;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by akulak on 8/5/17.
 */
public class DailyReconciliationProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    DailyReconciliationProcessor dailyReconciliationProcessor;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    CamelContext camelContext;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    RouteController routeController;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Test
    public void processInput() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(message);
        List<DailyReconcilationRecord> dailyReconcilationRecords=new ArrayList<>();
        dailyReconcilationRecords.add(getDailyReconcilationRecord("12345","1",RecapConstants.GFA_STATUS_IN));
        dailyReconcilationRecords.add(getDailyReconcilationRecord("2345","1",RecapConstants.GFA_STATUS_IN ));
        Mockito.when(message.getBody()).thenReturn(dailyReconcilationRecords);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.when(requestItemDetailsRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(saveRequestItemEntity(1, getItemEntity())));
        dailyReconciliationProcessor.processInput(exchange);
    }

    @Test
    public void processInputRequestId() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(message);
        List<DailyReconcilationRecord> dailyReconcilationRecords=new ArrayList<>();
        dailyReconcilationRecords.add(getDailyReconcilationRecord("12345",null, RecapConstants.GFA_STATUS_SCH_ON_REFILE_WORK_ORDER));
        dailyReconcilationRecords.add(getDailyReconcilationRecord("23451",null,RecapConstants.GFA_STATUS_SCH_ON_REFILE_WORK_ORDER));
        Mockito.when(message.getBody()).thenReturn(dailyReconcilationRecords);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
       Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(Arrays.asList(getItemEntity()));
        dailyReconciliationProcessor.processInput(exchange);
    }


    @Test
    public void processInputException() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(message);
        List<DailyReconcilationRecord> dailyReconcilationRecords=new ArrayList<>();
        DailyReconcilationRecord dailyReconcilationRecord = getDailyReconcilationRecord("12345","1","IN");
        dailyReconcilationRecords.add(dailyReconcilationRecord);
        Mockito.when(message.getBody()).thenReturn(dailyReconcilationRecords);
        Mockito.when(camelContext.getRouteController()).thenThrow(NullPointerException.class);
        dailyReconciliationProcessor.processInput(exchange);
    }

    private DailyReconcilationRecord getDailyReconcilationRecord(String barcode,String requestId,String status) {
        DailyReconcilationRecord dailyReconcilationRecord=new DailyReconcilationRecord();
        dailyReconcilationRecord.setCustomerCode("PA");
        dailyReconcilationRecord.setRequestId(requestId);
        dailyReconcilationRecord.setBarcode(barcode);
        dailyReconcilationRecord.setStopCode("PA");
        dailyReconcilationRecord.setPatronId("2");
        dailyReconcilationRecord.setCreateDate(new Date().toString());
        dailyReconcilationRecord.setOwningInst("1");
        dailyReconcilationRecord.setLastUpdatedDate(new Date().toString());
        dailyReconcilationRecord.setRequestingInst("1");
        dailyReconcilationRecord.setDeliveryMethod("test");
        dailyReconcilationRecord.setStatus(status);
        dailyReconcilationRecord.setErrorCode("");
        dailyReconcilationRecord.setErrorNote("");
        return dailyReconcilationRecord;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(new Random().nextInt());
        itemEntity.setBarcode("b3");
        itemEntity.setCustomerCode("c1");
        itemEntity.setCallNumber("cn1");
        itemEntity.setCallNumberType("ct1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCopyNumber(1);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("ut");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("ut");
        itemEntity.setUseRestrictions("no");
        itemEntity.setVolumePartYear("v3");
        itemEntity.setOwningInstitutionItemId(String.valueOf(new Random().nextInt()));
        itemEntity.setItemStatusEntity(getItemStatusEntity());
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        itemEntity.setDeleted(false);
        return itemEntity;
    }

    public RequestItemEntity saveRequestItemEntity(Integer itemId,ItemEntity itemEntity){
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(itemId);
        requestItemEntity.setId(new Random().nextInt());
        requestItemEntity.setRequestTypeId(1);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setStopCode("PA");
        requestItemEntity.setPatronId("45678912");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setEmailId("test@mail");
        requestItemEntity.setRequestStatusId(1);
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setInstitutionEntity(getInstitutionEntity());
        requestItemEntity.setRequestTypeEntity(getRequestTypeEntity());
        requestItemEntity.setItemEntity(itemEntity);
        return requestItemEntity;
    }

    private RequestTypeEntity getRequestTypeEntity() {
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setId(1);
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        return requestTypeEntity;
    }

    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("Available");
        return itemStatusEntity;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        return institutionEntity;
    }

}
