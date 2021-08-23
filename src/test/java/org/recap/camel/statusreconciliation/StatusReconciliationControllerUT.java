package org.recap.camel.statusreconciliation;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.controller.StatusReconciliationController;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.service.statusreconciliation.StatusReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 2/6/17.
 */
public class StatusReconciliationControllerUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(StatusReconciliationController.class);

    @InjectMocks
    StatusReconciliationController statusReconciliationController;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private ProducerTemplate producer;

    @Mock
    private RequestItemDetailsRepository mockedRequestItemDetailsRepository;

    @Mock
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    StatusReconciliationService statusReconciliationService;

    @Mock
    StatusReconciliationCSVRecord statusReconciliationCSVRecord;

    @Mock
    StatusReconciliationErrorCSVRecord statusReconciliationErrorCSVRecord;

    @Test
    public void testStatusReconciliation(){
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(Mockito.anyString())).thenReturn(getItemStatusEntity());
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(getRequestStatusEntity(1,ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED));
        requestStatusEntityList.add(getRequestStatusEntity(3,ScsbCommonConstants.REQUEST_STATUS_EDD));
        requestStatusEntityList.add(getRequestStatusEntity(5,ScsbCommonConstants.REQUEST_STATUS_CANCELED));
        requestStatusEntityList.add(getRequestStatusEntity(9,ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD));
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCodeIn(Mockito.any())).thenReturn(requestStatusEntityList);
        List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecords=new ArrayList<>();
       statusReconciliationErrorCSVRecords.add(statusReconciliationErrorCSVRecord);
        Map<String, List<StatusReconciliationErrorCSVRecord>> imsLocationsStatusReconciliationErrorList=new HashMap<>();
        imsLocationsStatusReconciliationErrorList.put("",statusReconciliationErrorCSVRecords);
        ReflectionTestUtils.invokeMethod(statusReconciliationController,"sendStatusReconciliationErrorRecordsToQueueAndEmail",imsLocationsStatusReconciliationErrorList,"");
        ReflectionTestUtils.setField(statusReconciliationController,"batchSize",100);
        ReflectionTestUtils.setField(statusReconciliationController,"statusReconciliationDayLimit",100);
        ReflectionTestUtils.setField(statusReconciliationController,"statusReconciliationLasBarcodeLimit",100);
        Mockito.when(itemDetailsRepository.getNotAvailableItemsCount(Mockito.anyInt(),Mockito.anyList(),Mockito.anyInt())).thenReturn(1l);
        Mockito.when(itemDetailsRepository.getNotAvailableItems(Mockito.anyInt(),Mockito.anyList(),Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(Arrays.asList(getItemEntity()));
        List<StatusReconciliationCSVRecord> itemStatusComparison=new ArrayList<>();
        itemStatusComparison.add(statusReconciliationCSVRecord);
        Mockito.when(statusReconciliationCSVRecord.getImsLocation()).thenReturn("RECAP");
        Mockito.when(statusReconciliationService.itemStatusComparison(Mockito.anyList(),Mockito.anyList(),0)).thenReturn(itemStatusComparison);
        ResponseEntity responseEntity = statusReconciliationController.itemStatusReconciliation();
        assertEquals("Success", responseEntity.getBody().toString());
    }

    private RequestStatusEntity getRequestStatusEntity(int id,String status) {
        RequestStatusEntity requestStatusEntity=new RequestStatusEntity();
        requestStatusEntity.setId(id);
        requestStatusEntity.setRequestStatusCode(status);
        requestStatusEntity.setRequestStatusDescription(status);
        return requestStatusEntity;
    }

    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity=new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode(ScsbCommonConstants.AVAILABLE);
        itemStatusEntity.setStatusDescription(ScsbCommonConstants.AVAILABLE);
        return itemStatusEntity;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("843617540");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemAvailabilityStatusId(2);
        itemEntity.setDeleted(false);
        return itemEntity;
    }
}
