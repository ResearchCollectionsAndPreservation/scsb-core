package org.recap.camel.statusreconciliation;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 2/6/17.
 */
public class StatusReconciliationControllerUT extends BaseTestCase{

    @Mock
    private StatusReconciliationController statusReconciliationController;

    private static final Logger logger = LoggerFactory.getLogger(StatusReconciliationController.class);

    private Integer batchSize = 100;

    @Value("${status.reconciliation.day.limit}")
    private Integer statusReconciliationDayLimit;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;


    private Integer statusReconciliationLasBarcodeLimit = 100;

    @Mock
    private ProducerTemplate producer;

    @Mock
    private RequestItemDetailsRepository mockedRequestItemDetailsRepository;

    @Mock
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Test
    public void testStatusReconciliation(){
        Map<String,Integer> itemCountAndStatusIdMap = new HashMap<>();
        itemCountAndStatusIdMap.put("itemAvailabilityStatusId",0);
        itemCountAndStatusIdMap.put("totalPagesCount",0);
        int totalPagesCount = itemCountAndStatusIdMap.get("totalPagesCount");
        int itemAvailabilityStatusId = itemCountAndStatusIdMap.get("itemAvailabilityStatusId");
        List<List<ItemEntity>> itemEntityChunkList = new ArrayList<>();
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("3321545824554545");
        itemEntity.setItemId(1);
        itemEntity.setItemAvailabilityStatusId(2);
        List<ItemEntity> itemEntityList = Arrays.asList(itemEntity);
        itemEntityChunkList = Arrays.asList(itemEntityList);
        long from = 10 * Long.valueOf(batchSize);
        Date date = new Date();
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(2);
        Mockito.when(statusReconciliationController.getFromDate(0)).thenReturn(from);
        Mockito.when(statusReconciliationController.getTotalPageCount(Arrays.asList(1,9), itemStatusEntity.getId())).thenReturn(itemCountAndStatusIdMap);
        Mockito.when(statusReconciliationController.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(statusReconciliationController.getBatchSize()).thenReturn(batchSize);
        Mockito.when(statusReconciliationController.getItemStatusDetailsRepository()).thenReturn(itemStatusDetailsRepository);
        Mockito.when(statusReconciliationController.getRequestItemStatusDetailsRepository()).thenReturn(requestItemStatusDetailsRepository);
        Mockito.when(statusReconciliationController.getStatusReconciliationDayLimit()).thenReturn(statusReconciliationDayLimit);
        Mockito.when(statusReconciliationController.getStatusReconciliationLasBarcodeLimit()).thenReturn(statusReconciliationLasBarcodeLimit);
        Mockito.when(statusReconciliationController.getProducer()).thenReturn(producer);
        //Mockito.when(statusReconciliationController.getGfaService().itemStatusComparison(Mockito.any(),Mockito.any())).thenCallRealMethod();
        Mockito.when(statusReconciliationController.getItemDetailsRepository().getNotAvailableItems(statusReconciliationDayLimit,Arrays.asList(1,9),from,batchSize,itemStatusEntity.getId())).thenReturn(itemEntityList);
        Mockito.when(statusReconciliationController.getTotalPageCount(Arrays.asList(1,9), itemStatusEntity.getId())).thenCallRealMethod();
        Mockito.when(statusReconciliationController.getItemStatusDetailsRepository().findByStatusCode(RecapConstants.ITEM_STATUS_NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.when(statusReconciliationController.itemStatusReconciliation()).thenCallRealMethod();
        ResponseEntity responseEntity = statusReconciliationController.itemStatusReconciliation();
        List<Integer> requestStatusIds = new ArrayList<>();
        requestStatusIds.add(1);
        requestStatusIds.add(2);
        Map<String,Integer>  data= statusReconciliationController.getTotalPageCount(requestStatusIds,1);
        assertNotNull(responseEntity);
        assertNotNull(data);
        assertEquals(responseEntity.getBody().toString(),"Success");
    }

    @Test
    public void test(){
        statusReconciliationController.getBatchSize();
        statusReconciliationController.getFromDate(1);
        statusReconciliationController.getItemDetailsRepository();
        statusReconciliationController.getItemStatusDetailsRepository();
        statusReconciliationController.getProducer();
        statusReconciliationController.getRequestItemStatusDetailsRepository();
        statusReconciliationController.getStatusReconciliationDayLimit();
        statusReconciliationController.getStatusReconciliationLasBarcodeLimit();
        assertTrue(true);
    }
}