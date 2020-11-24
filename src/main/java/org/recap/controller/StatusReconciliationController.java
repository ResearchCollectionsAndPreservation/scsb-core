package org.recap.controller;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.service.statusreconciliation.StatusReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hemalathas on 1/6/17.
 */
@RestController
@RequestMapping("/statusReconciliation")
@Slf4j
public class StatusReconciliationController {

    @Value("${scsb.circ.url}")
    private String scsbCircUrl;

    @Value("${status.reconciliation.batch.size}")
    private Integer batchSize;

    @Value("${status.reconciliation.day.limit}")
    private Integer statusReconciliationDayLimit;

    @Value("${status.reconciliation.las.barcode.limit}")
    private Integer statusReconciliationLasBarcodeLimit;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    private StatusReconciliationService statusReconciliationService;

    @Autowired
    private ProducerTemplate producer;

    /**
     * Gets batch size.
     *
     * @return the batch size
     */
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * Gets status reconciliation day limit.
     *
     * @return the status reconciliation day limit
     */
    public Integer getStatusReconciliationDayLimit() {
        return statusReconciliationDayLimit;
    }

    /**
     * Gets item status details repository.
     *
     * @return the item status details repository
     */
    public ItemStatusDetailsRepository getItemStatusDetailsRepository() {
        return itemStatusDetailsRepository;
    }

    /**
     * Gets item details repository.
     *
     * @return the item details repository
     */
    public ItemDetailsRepository getItemDetailsRepository() {
        return itemDetailsRepository;
    }


    /**
     * Get request item status details repository request item status details repository.
     *
     * @return the request item status details repository
     */
    public RequestItemStatusDetailsRepository getRequestItemStatusDetailsRepository() {
        return requestItemStatusDetailsRepository;
    }

    /**
     * Gets status reconciliation las barcode limit.
     *
     * @return the status reconciliation las barcode limit
     */
    public Integer getStatusReconciliationLasBarcodeLimit() {
        return statusReconciliationLasBarcodeLimit;
    }

    /**
     * Gets producer.
     *
     * @return the producer
     */
    public ProducerTemplate getProducer() {
        return producer;
    }

    /**
     * Get from date long.
     *
     * @param pageNum the page num
     * @return the long
     */
    public Long getFromDate(int pageNum) {
        return pageNum * Long.valueOf(getBatchSize());
    }

    /**
     * Prepare the item entites for the status reconciliation and
     * placing the status reconciliation csv records in the scsb active-mq.
     *
     * @return the response entity
     */
    @GetMapping(value = "/itemStatusReconciliation")
    public ResponseEntity<String> itemStatusReconciliation() {
        ItemStatusEntity itemStatusEntity = getItemStatusDetailsRepository().findByStatusCode(RecapConstants.ITEM_STATUS_NOT_AVAILABLE);
        List<String> requestStatusCodes = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        List<RequestStatusEntity> requestStatusEntityList = getRequestItemStatusDetailsRepository().findByRequestStatusCodeIn(requestStatusCodes);
        List<Integer> requestStatusIds = requestStatusEntityList.stream().map(RequestStatusEntity::getId).collect(Collectors.toList());
        log.info("status reconciliation request ids : {} ", requestStatusIds);
        Map<String, Integer> itemCountAndStatusIdMap = getTotalPageCount(requestStatusIds, itemStatusEntity.getId());
        if (itemCountAndStatusIdMap.size() > 0) {
            int totalPagesCount = itemCountAndStatusIdMap.get("totalPagesCount");
            log.info("status reconciliation total page count: {}", totalPagesCount);
            List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList = new ArrayList<>();
            List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecords = new ArrayList<>();
            for (int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
                long from = getFromDate(pageNum);
                List<ItemEntity> itemEntityList = getItemDetailsRepository().getNotAvailableItems(getStatusReconciliationDayLimit(), requestStatusIds, from, getBatchSize(), itemStatusEntity.getId());
                log.info("items fetched from data base-----> {}", itemEntityList.size());
                List<List<ItemEntity>> itemEntityChunkList = Lists.partition(itemEntityList, getStatusReconciliationLasBarcodeLimit());
                statusReconciliationCSVRecordList.addAll(statusReconciliationService.itemStatusComparison(itemEntityChunkList, statusReconciliationErrorCSVRecords));
                log.info("status reconciliation page num: {} and records {} processed", pageNum, from + getBatchSize());
            }
            getProducer().sendBodyAndHeader(RecapConstants.STATUS_RECONCILIATION_REPORT, statusReconciliationCSVRecordList, RecapConstants.FOR, RecapConstants.STATUS_RECONCILIATION);
            getProducer().sendBodyAndHeader(RecapConstants.STATUS_RECONCILIATION_REPORT, statusReconciliationErrorCSVRecords, RecapConstants.FOR, RecapConstants.STATUS_RECONCILIATION_FAILURE);
        }
        return new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK);
    }

    /**
     * Get total page count for the status reconciliation.
     *
     * @param requestStatusIds
     * @param itemStatusId
     * @return the map
     */
    public Map<String, Integer> getTotalPageCount(List<Integer> requestStatusIds, Integer itemStatusId) {
        Map<String, Integer> itemCountAndStatusIdMap = new HashMap<>();
        long itemCount = getItemDetailsRepository().getNotAvailableItemsCount(getStatusReconciliationDayLimit(), requestStatusIds, itemStatusId);
        log.info("status reconciliation total item records count: {}", itemCount);
        if (itemCount > 0) {
            int totalPagesCount = (int) (itemCount / getBatchSize());
            itemCountAndStatusIdMap.put("totalPagesCount", totalPagesCount);
        }
        return itemCountAndStatusIdMap;
    }
}
