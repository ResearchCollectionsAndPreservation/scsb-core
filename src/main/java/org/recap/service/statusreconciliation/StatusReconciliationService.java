package org.recap.service.statusreconciliation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.ItemRefileRequest;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.SolrDocIndexService;
import org.recap.spring.SwaggerAPIProvider;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatusReconciliationService {

    @Value("${scsb.gateway.url}")
    private String scsbUrl;

    @Value("${scsb.circ.url}")
    private String scsbCircUrl;

    @Value("${status.reconciliation.day.limit}")
    private Integer statusReconciliationDayLimit;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    private SolrDocIndexService solrDocIndexService;

    /**
     * To compare the item status for the status reconciliation process with LAS .
     *
     * @param itemEntityChunkList                    the item entity chunk list
     * @param statusReconciliationErrorCSVRecordList the status reconciliation error csv record list
     * @return the list
     */
    public List<StatusReconciliationCSVRecord> itemStatusComparison(List<List<ItemEntity>> itemEntityChunkList, List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecordList) {
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList = new ArrayList<>();
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        for (List<ItemEntity> itemEntities : itemEntityChunkList) {
            List<String> lasNotAvailableStatusList = RecapConstants.getGFAStatusNotAvailableList();
            GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGFAItemStatusCheckResponse(itemEntities);
            if (gfaItemStatusCheckResponse != null && gfaItemStatusCheckResponse.getDsitem() != null && gfaItemStatusCheckResponse.getDsitem().getTtitem() != null) {
                List<Ttitem> ttitemList = gfaItemStatusCheckResponse.getDsitem().getTtitem();
                String lasStatus = null;
                StatusReconciliationErrorCSVRecord statusReconciliationErrorCSVRecord = new StatusReconciliationErrorCSVRecord();
                for (ItemEntity itemEntity : itemEntities) {
                    boolean isBarcodeAvailableForErrorReport = false;
                    for (Ttitem ttitem : ttitemList) {
                        if (itemEntity.getBarcode().equalsIgnoreCase(ttitem.getItemBarcode())) {
                            isBarcodeAvailableForErrorReport = true;
                            lasStatus = ttitem.getItemStatus();
                            boolean isNotAvailable = false;
                            for (String status : lasNotAvailableStatusList) {
                                if (StringUtils.startsWithIgnoreCase(lasStatus, status)) {
                                    isNotAvailable = true;
                                }
                            }
                            if (!isNotAvailable) {
                                processMismatchStatus(statusReconciliationCSVRecordList, itemChangeLogEntityList, lasStatus, itemEntity);
                            }
                            break;
                        }
                    }
                    if (!isBarcodeAvailableForErrorReport) {
                        statusReconciliationErrorCSVRecord.setBarcode(itemEntity.getBarcode());
                        statusReconciliationErrorCSVRecord.setInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
                        statusReconciliationErrorCSVRecord.setReasonForFailure(RecapConstants.BARCODE_NOT_FOUND_IN_LAS);
                        statusReconciliationErrorCSVRecordList.add(statusReconciliationErrorCSVRecord);
                    }
                }
            }
            itemChangeLogDetailsRepository.saveAll(itemChangeLogEntityList);
            itemChangeLogDetailsRepository.flush();
        }
        return statusReconciliationCSVRecordList;
    }

    private GFAItemStatusCheckResponse getGFAItemStatusCheckResponse(List<ItemEntity> itemEntities) {
        ResponseEntity<GFAItemStatusCheckResponse> responseEntity = null;
        try {
            HttpEntity httpEntity = new HttpEntity(commonUtil.getBarcodesList(itemEntities));
            RestTemplate restTemplate = new RestTemplate();
            responseEntity = restTemplate.exchange(scsbCircUrl + RecapConstants.GFA_MULTIPLE_ITEM_STATUS_URL, HttpMethod.POST, httpEntity, GFAItemStatusCheckResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseEntity.getBody();
    }

    private void processMismatchStatus(List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList, List<ItemChangeLogEntity> itemChangeLogEntityList, String lasStatus, ItemEntity itemEntity) {
        StatusReconciliationCSVRecord statusReconciliationCSVRecord = new StatusReconciliationCSVRecord();
        List<String> requestStatusCodes = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        List<RequestStatusEntity> requestStatusEntityList = requestItemStatusDetailsRepository.findByRequestStatusCodeIn(requestStatusCodes);
        List<Integer> requestStatusIds = requestStatusEntityList.stream().map(RequestStatusEntity::getId).collect(Collectors.toList());
        List<Integer> requestid = requestItemDetailsRepository.getRequestItemEntitiesBasedOnDayLimit(itemEntity.getItemId(), requestStatusIds, statusReconciliationDayLimit);
        List<RequestItemEntity> requestItemEntityList = requestItemDetailsRepository.findByIdIn(requestid);
        List<String> barcodeList = new ArrayList<>();
        List<Integer> requestIdList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId()).orElse(new ItemStatusEntity());
        if (!requestItemEntityList.isEmpty()) {
            for (RequestItemEntity requestItemEntity : requestItemEntityList) {
                if (!requestItemEntity.getRequestStatusEntity().getRequestStatusCode().equalsIgnoreCase(RecapCommonConstants.REQUEST_STATUS_CANCELED)) {
                    statusReconciliationCSVRecord = getStatusReconciliationCSVRecord(lasStatus, itemEntity, barcodeList, requestIdList, simpleDateFormat, itemStatusEntity, requestItemEntity);
                } else {
                    if (StringUtils.containsIgnoreCase(requestItemEntity.getNotes(), "Cancel requested")) {
                        statusReconciliationCSVRecord = getStatusReconciliationCSVRecord(lasStatus, itemEntity, barcodeList, requestIdList, simpleDateFormat, itemStatusEntity, requestItemEntity);
                    } else {
                        RequestStatusEntity byRequestStatusCode = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_REFILED);
                        requestItemEntity.setRequestStatusId(byRequestStatusCode.getId());
                        requestItemEntity.setLastUpdatedDate(new Date());
                        requestItemDetailsRepository.save(requestItemEntity);
                        log.info("request status updated from cancel to refile for the request id : {}", requestItemEntity.getId());
                    }
                }
            }
        } else {
            statusReconciliationCSVRecord = getStatusReconciliationCSVRecord(itemEntity.getBarcode(), "No", null, lasStatus, simpleDateFormat.format(new Date()), itemStatusEntity);
            itemDetailsRepository.updateAvailabilityStatus(1, RecapConstants.GUEST_USER, itemEntity.getBarcode());
            ItemChangeLogEntity itemChangeLogEntity = saveItemChangeLogEntity(itemEntity.getItemId(), itemEntity.getBarcode());
            itemChangeLogEntityList.add(itemChangeLogEntity);
            solrDocIndexService.updateSolrIndex(itemEntity);
            log.info("found mismatch in item status and updated availability status for the item barcode: {}", itemEntity.getBarcode());
        }
        if (!barcodeList.isEmpty() && !requestIdList.isEmpty()) {
            reFileItems(barcodeList, requestIdList);
        }
        statusReconciliationCSVRecordList.add(statusReconciliationCSVRecord);
    }

    private StatusReconciliationCSVRecord getStatusReconciliationCSVRecord(String lasStatus, ItemEntity itemEntity, List<String> barcodeList, List<Integer> requestIdList, SimpleDateFormat simpleDateFormat, ItemStatusEntity itemStatusEntity, RequestItemEntity requestItemEntity) {
        StatusReconciliationCSVRecord statusReconciliationCSVRecord = getStatusReconciliationCSVRecord(itemEntity.getBarcode(), "yes", requestItemEntity.getId().toString(), lasStatus, simpleDateFormat.format(new Date()), itemStatusEntity);
        barcodeList.add(itemEntity.getBarcode());
        requestIdList.add(requestItemEntity.getId());
        log.info("found mismatch in item status and refilled for the item id: {}", requestItemEntity.getItemId());
        return statusReconciliationCSVRecord;
    }

    @Async
    public void reFileItems(List<String> itemBarcodes, List<Integer> requestIdList) {
        ItemRefileRequest itemRequestInfo = new ItemRefileRequest();
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(itemBarcodes);
            itemRequestInfo.setRequestIds(requestIdList);
            HttpEntity request = new HttpEntity(itemRequestInfo, getHttpHeadersAuth());
            restTemplate.exchange(scsbUrl + RecapConstants.SERVICE_PATH.REFILE_ITEM, HttpMethod.POST, request, ItemRefileResponse.class);
        } catch (Exception ex) {
            log.error(RecapConstants.EXCEPTION, ex);
        }
    }

    /**
     * For the given input this method prepares the status reconciliation csv record.
     *
     * @param barcode          the barcode
     * @param availability     the availability
     * @param requestId        the request id
     * @param statusInLas      the status in las
     * @param dateTime         the date time
     * @param itemStatusEntity the item status entity
     * @return the status reconciliation csv record
     */
    public StatusReconciliationCSVRecord getStatusReconciliationCSVRecord(String barcode, String availability, String requestId, String statusInLas, String dateTime, ItemStatusEntity itemStatusEntity) {
        StatusReconciliationCSVRecord statusReconciliationCSVRecord = new StatusReconciliationCSVRecord();
        statusReconciliationCSVRecord.setBarcode(barcode);
        statusReconciliationCSVRecord.setRequestAvailability(availability);
        statusReconciliationCSVRecord.setRequestId(requestId);
        statusReconciliationCSVRecord.setStatusInLas(statusInLas);
        if (itemStatusEntity != null) {
            statusReconciliationCSVRecord.setStatusInScsb(itemStatusEntity.getStatusDescription());
        }
        statusReconciliationCSVRecord.setDateTime(dateTime);
        return statusReconciliationCSVRecord;
    }

    private ItemChangeLogEntity saveItemChangeLogEntity(Integer requestId, String barcode) {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        String notes = "ItemBarcode:" + barcode + " , " + "ItemAvailabilityStatusChange" + RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_ROLLBACK;
        itemChangeLogEntity.setUpdatedBy(RecapConstants.GUEST_USER);
        itemChangeLogEntity.setUpdatedDate(new Date());
        itemChangeLogEntity.setOperationType(RecapConstants.STATUS_RECONCILIATION_CHANGE_LOG_OPERATION_TYPE);
        itemChangeLogEntity.setRecordId(requestId);
        itemChangeLogEntity.setNotes(notes);
        return itemChangeLogEntity;
    }

    private HttpHeaders getHttpHeadersAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RecapCommonConstants.API_KEY, SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        return headers;
    }
}
