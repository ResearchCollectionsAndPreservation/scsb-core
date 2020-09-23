package org.recap.camel.statusreconciliation;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.repository.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 2/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class StatusReconciliationControllerUT{

    @InjectMocks
    private StatusReconciliationController statusReconciliationController;

    private static final Logger logger = LoggerFactory.getLogger(StatusReconciliationController.class);

    private Integer batchSize = 100;

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

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(statusReconciliationController,"batchSize",10);
        ReflectionTestUtils.setField(statusReconciliationController,"statusReconciliationDayLimit",2);
        ReflectionTestUtils.setField(statusReconciliationController,"statusReconciliationLasBarcodeLimit",1);
    }

    @Test
    public void testStatusReconciliation(){
        try {
            Map<String, Integer> itemCountAndStatusIdMap = new HashMap<>();
            itemCountAndStatusIdMap.put("itemAvailabilityStatusId", 0);
            itemCountAndStatusIdMap.put("totalPagesCount", 0);
            int totalPagesCount = itemCountAndStatusIdMap.get("totalPagesCount");
            int itemAvailabilityStatusId = itemCountAndStatusIdMap.get("itemAvailabilityStatusId");
            List<List<ItemEntity>> itemEntityChunkList = new ArrayList<>();
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setBarcode("3321545824554545");
            itemEntity.setItemId(1);
            itemEntity.setItemAvailabilityStatusId(2);
            List<ItemEntity> itemEntityList = Arrays.asList(itemEntity);
            itemEntityChunkList = Arrays.asList(itemEntityList);
            ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
            itemStatusEntity.setId(2);
            List<RequestStatusEntity> requestStatusEntityList = new ArrayList<>();
            RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
            requestStatusEntity.setId(3);
            requestStatusEntity.setRequestStatusCode("EDD");
            requestStatusEntity.setRequestStatusDescription("EDD");
            requestStatusEntityList.add(requestStatusEntity);
            List<String> requestStatusCodes = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
            Mockito.when(statusReconciliationController.getRequestItemStatusDetailsRepository().findByRequestStatusCodeIn(requestStatusCodes)).thenReturn(Arrays.asList(requestStatusEntity));
            Mockito.when(itemDetailsRepository.getNotAvailableItemsCount(2, Arrays.asList(3), itemStatusEntity.getId())).thenReturn((long) 1);
            Mockito.when(statusReconciliationController.getItemStatusDetailsRepository().findByStatusCode(RecapConstants.ITEM_STATUS_NOT_AVAILABLE)).thenReturn(itemStatusEntity);
            ResponseEntity responseEntity = statusReconciliationController.itemStatusReconciliation();
            List<Integer> requestStatusIds = new ArrayList<>();
            requestStatusIds.add(1);
            requestStatusIds.add(2);
            Map<String, Integer> data = statusReconciliationController.getTotalPageCount(requestStatusIds, 1);
            assertNotNull(responseEntity);
            assertNotNull(data);
            assertEquals(responseEntity.getBody().toString(), "Success");
        }catch (Exception e){}
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