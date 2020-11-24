package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.converter.AccessionXmlToBibEntityConverterInterface;
import org.recap.converter.XmlToBibEntityConverterFactory;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.accession.AccessionDAO;
import org.recap.service.accession.AccessionValidationService;
import org.recap.service.accession.DummyDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AccessionUtil {

    private static final Logger logger = LoggerFactory.getLogger(AccessionUtil.class);

    @Autowired
    DummyDataService dummyDataService;

    @Autowired
    MarcUtil marcUtil;

    @Autowired
    DBReportUtil dbReportUtil;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    XmlToBibEntityConverterFactory xmlToBibEntityConverterFactory;

    @Autowired
    AccessionValidationService accessionValidationService;

    @Autowired
    AccessionDAO accessionDAO;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    @Value("${scsb.solr.doc.url}")
    private String scsbSolrClientUrl;

    private RestTemplate restTemplate;
    /**
     * Gets rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        if(restTemplate == null){
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }


    private Map<String,Integer> institutionEntityMap;


    public Map<String, Object> processAndValidateBibliographicEntity(Record bibRecord, Integer owningInstitutionId, Date currentDate, StringBuilder errorMessage) {
        int failedBibCount = 0;
        int successBibCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        Map<String, Object> map = new HashMap<>();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();

        if (owningInstitutionId != null) {
            bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        } else {
            errorMessage.append("Owning Institution Id cannot be null").append(",");
        }
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCreatedDate(currentDate);
        bibliographicEntity.setCreatedBy(RecapCommonConstants.ACCESSION);
        bibliographicEntity.setLastUpdatedDate(currentDate);
        bibliographicEntity.setLastUpdatedBy(RecapCommonConstants.ACCESSION);


    // FORMAT SPECIFIC

        String owningInstitutionBibId = marcUtil.getControlFieldValue(bibRecord, "001");
        if (StringUtils.isNotBlank(owningInstitutionBibId)) {
            bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        } else {
            errorMessage.append("Owning Institution Bib Id cannot be null").append(",");
        }
        String bibContent = marcUtil.writeMarcXml(bibRecord);
        if (StringUtils.isNotBlank(bibContent)) {
            bibliographicEntity.setContent(bibContent.getBytes());
        } else {
            errorMessage.append("Bib Content cannot be empty").append(",");
        }

        boolean subFieldExistsFor245 = marcUtil.isSubFieldExists(bibRecord, "245");
        if (!subFieldExistsFor245) {
            errorMessage.append("Atleast one subfield should be there for 245 tag").append(",");
        }
        Leader leader = bibRecord.getLeader();
        if(leader == null){
            errorMessage.append(" Leader field is missing").append(",");
        } else {
            String leaderValue = bibRecord.getLeader().toString();
            if (!(StringUtils.isNotBlank(leaderValue) && leaderValue.length() == 24)) {
                errorMessage.append("Leader Field value should be 24 characters").append(",");
            }
        }

        if(owningInstitutionId != null && StringUtils.isNotBlank(owningInstitutionBibId)){
            BibliographicEntity existBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(owningInstitutionId,owningInstitutionBibId);
            if(null != existBibliographicEntity){
                exitsBibCount = 1;
            }
        }
        List<ReportDataEntity> reportDataEntities = null;

        if (errorMessage.toString().length() > 1) {
            if(exitsBibCount == 0){
                failedBibCount = failedBibCount+1;
            }
            reasonForFailureBib = errorMessage.toString();
            reportDataEntities = dbReportUtil.generateBibFailureReportEntity(bibliographicEntity, bibRecord);
            ReportDataEntity errorReportDataEntity = new ReportDataEntity();
            errorReportDataEntity.setHeaderName(RecapCommonConstants.ERROR_DESCRIPTION);
            errorReportDataEntity.setHeaderValue(errorMessage.toString());
            reportDataEntities.add(errorReportDataEntity);
        }else if(exitsBibCount == 0){
            successBibCount = successBibCount+1;
        }

        map.put(RecapCommonConstants.FAILED_BIB_COUNT , failedBibCount);
        map.put(RecapCommonConstants.REASON_FOR_BIB_FAILURE , reasonForFailureBib);
        map.put(RecapCommonConstants.BIBLIOGRAPHICENTITY, bibliographicEntity);
        map.put(RecapCommonConstants.SUCCESS_BIB_COUNT,successBibCount);
        map.put(RecapCommonConstants.EXIST_BIB_COUNT,exitsBibCount);
        return map;
    }

    /**
     * Create report data entity list for accessioned item.
     *
     * @param accessionRequest the accession request
     * @param response         the response
     * @return the list
     */
    public List<ReportDataEntity> createReportDataEntityList(AccessionRequest accessionRequest, String response) {
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        if (StringUtils.isNotBlank(accessionRequest.getCustomerCode())) {
            ReportDataEntity reportDataEntityCustomerCode = new ReportDataEntity();
            reportDataEntityCustomerCode.setHeaderName(RecapCommonConstants.CUSTOMER_CODE);
            reportDataEntityCustomerCode.setHeaderValue(accessionRequest.getCustomerCode());
            reportDataEntityList.add(reportDataEntityCustomerCode);
        }
        if (StringUtils.isNotBlank(accessionRequest.getItemBarcode())) {
            ReportDataEntity reportDataEntityItemBarcode = new ReportDataEntity();
            reportDataEntityItemBarcode.setHeaderName(RecapCommonConstants.ITEM_BARCODE);
            reportDataEntityItemBarcode.setHeaderValue(accessionRequest.getItemBarcode());
            reportDataEntityList.add(reportDataEntityItemBarcode);
        }
        ReportDataEntity reportDataEntityMessage = new ReportDataEntity();
        reportDataEntityMessage.setHeaderName(RecapCommonConstants.MESSAGE);
        reportDataEntityMessage.setHeaderValue(response);
        reportDataEntityList.add(reportDataEntityMessage);
        return reportDataEntityList;
    }


    /**
     * Sets accession response.
     *
     * @param accessionResponseList the accession response list
     * @param itemBarcode           the item barcode
     * @param message               the message
     */
    public void setAccessionResponse(Set<AccessionResponse> accessionResponseList, String itemBarcode, String message) {
        AccessionResponse accessionResponse = new AccessionResponse();
        accessionResponse.setItemBarcode(itemBarcode);
        accessionResponse.setMessage(message);
        accessionResponseList.add(accessionResponse);
    }


    /**
     * This method is used to find the owning institution code based on the customer code parameter value.
     *
     * @param customerCode
     * @return
     */
    public String getOwningInstitution(String customerCode) {
        String owningInstitution = null;
        try {
            CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCode(customerCode);
            if (null != customerCodeEntity) {
                owningInstitution = customerCodeEntity.getInstitutionEntity().getInstitutionCode();
            }
        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
        }
        return owningInstitution;
    }


    /**
     * This method is used to create dummy record if the item barcode is not found.
     * @param response
     * @param owningInstitution
     * @param reportDataEntityList
     * @param accessionRequest
     */
    public String createDummyRecordIfAny(String response, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) {
        String message = response;
        if (response != null && (response.contains(RecapConstants.ITEM_BARCODE_NOT_FOUND) ||
                response.contains(RecapConstants.INVALID_MARC_XML_ERROR_MSG)) ) {
            BibliographicEntity fetchBibliographicEntity = getBibEntityUsingBarcodeForIncompleteRecord(accessionRequest.getItemBarcode());
            if (fetchBibliographicEntity == null) {
                String dummyRecordResponse = createDummyRecord(accessionRequest, owningInstitution);
                message = response+", "+dummyRecordResponse;
            } else {
                message = RecapConstants.ITEM_BARCODE_ALREADY_ACCESSIONED_MSG;
            }
            reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, message));
        }
        return message;
    }

    /**
     * This method is used to get the BibliographicEntity from ItemEntity list using item barcode.
     * @param itemBarcode
     * @return
     */
    private BibliographicEntity getBibEntityUsingBarcodeForIncompleteRecord(String itemBarcode){
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add(itemBarcode);
        List<ItemEntity> itemEntityList = itemDetailsRepository.findByBarcodeIn(itemBarcodeList);
        BibliographicEntity fetchedBibliographicEntity = null;
        if(itemEntityList != null && !itemEntityList.isEmpty() && itemEntityList.get(0).getBibliographicEntities() != null){
            fetchedBibliographicEntity = itemEntityList.get(0).getBibliographicEntities().get(0);
        }
        return fetchedBibliographicEntity;
    }

    /**
     * This method is used to create dummy record for bib in the database and index them in Solr.
     * @param accessionRequest
     * @param owningInstitution
     * @return
     */
    public String createDummyRecord(AccessionRequest accessionRequest, String owningInstitution) {
        String response;
        Integer owningInstitutionId = (Integer) getInstitutionEntityMap().get(owningInstitution);
        BibliographicEntity dummyBibliographicEntity = dummyDataService.createDummyDataAsIncomplete(owningInstitutionId,accessionRequest.getItemBarcode(),accessionRequest.getCustomerCode());
        indexData(Set.of(dummyBibliographicEntity.getBibliographicId()));
        response = RecapConstants.ACCESSION_DUMMY_RECORD;
        return response;
    }

    /**
     * Index data string.
     *
     * @param bibliographicIdList the bibliographic id list
     * @return the string
     */
    public String indexData(Set<Integer> bibliographicIdList){
        return getRestTemplate().postForObject(scsbSolrClientUrl + "solrIndexer/indexByBibliographicId", bibliographicIdList, String.class);
    }

    private synchronized Map getInstitutionEntityMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap();
            try {
                Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
                for (InstitutionEntity institutionEntity : institutionEntities) {
                    institutionEntityMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return institutionEntityMap;
    }

    /**
     * This method is used to update the incoming data to the existing bib or create a new bib and save them in database,
     * Once saved in database they are indexed in Solr.
     * @param record
     * @param owningInstitution
     * @param responseMapList
     * @param accessionRequest
     * @return
     */
    public synchronized String updateData(Object record, String owningInstitution, List<Map<String, String>> responseMapList, AccessionRequest accessionRequest, boolean isValidBoundWithRecord, boolean isFirstRecord){
        String response = null;
        AccessionXmlToBibEntityConverterInterface converter = xmlToBibEntityConverterFactory.getConverter(owningInstitution);
        if (null != converter) {
            Map responseMap = converter.convert(record, owningInstitution, accessionRequest);
            responseMapList.add(responseMap);
            StringBuilder errorMessage = (StringBuilder)responseMap.get("errorMessage");
            BibliographicEntity bibliographicEntity = (BibliographicEntity) responseMap.get(RecapCommonConstants.BIBLIOGRAPHICENTITY);
            String incompleteResponse = (String) responseMap.get(RecapConstants.INCOMPLETE_RESPONSE);
            if (errorMessage != null && errorMessage.length()==0) {//Valid bibliographic entity is returned for further processing
                if (bibliographicEntity != null) {
                    boolean isValidItemAndHolding = accessionValidationService.validateItemAndHolding(bibliographicEntity,isValidBoundWithRecord,isFirstRecord,errorMessage);
                    if (isValidItemAndHolding) {
                        BibliographicEntity savedBibliographicEntity = updateBibliographicEntity(bibliographicEntity);
                        if (null != savedBibliographicEntity) {
                            response = indexBibliographicRecord(savedBibliographicEntity.getBibliographicId());
                        }
                    } else {
                        response = errorMessage.toString();
                    }
                }
                if (StringUtils.isNotEmpty(response) && StringUtils.isNotEmpty(incompleteResponse) && RecapCommonConstants.SUCCESS.equalsIgnoreCase(response)){
                    return RecapConstants.SUCCESS_INCOMPLETE_RECORD;
                }
            } else{
                if(errorMessage != null) {
                    return RecapConstants.FAILED + RecapCommonConstants.HYPHEN + errorMessage.toString();
                }
                else {
                    return RecapConstants.FAILED;
                }
            }

        }
        return response;
    }

    /**
     * This method is used to index Bibliographic Record in solr and return a response.
     * @param bibliographicId
     * @return
     */
    private String indexBibliographicRecord(Integer bibliographicId) {
        String response;
        indexData(Set.of(bibliographicId));
        response = RecapCommonConstants.SUCCESS;
        return response;
    }

    /**
     *This method is used to update bibs if exists or create and save the bibs.
     * @param bibliographicEntity
     * @return
     */
    public BibliographicEntity updateBibliographicEntity(BibliographicEntity bibliographicEntity) {
        BibliographicEntity savedBibliographicEntity=null;
        BibliographicEntity fetchBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(),bibliographicEntity.getOwningInstitutionBibId());
        if(fetchBibliographicEntity ==null) { // New Bib Record
            savedBibliographicEntity = accessionDAO.saveBibRecord(bibliographicEntity);
        }else{ // Existing bib Record
            // Bib
            fetchBibliographicEntity.setContent(bibliographicEntity.getContent());
            fetchBibliographicEntity.setLastUpdatedBy(bibliographicEntity.getLastUpdatedBy());
            fetchBibliographicEntity.setLastUpdatedDate(bibliographicEntity.getLastUpdatedDate());
            fetchBibliographicEntity.setDeleted(bibliographicEntity.isDeleted());
            if (fetchBibliographicEntity.getCatalogingStatus().equals(RecapCommonConstants.INCOMPLETE_STATUS)) {
                fetchBibliographicEntity.setCatalogingStatus(bibliographicEntity.getCatalogingStatus());
            }

            // Holding
            List<HoldingsEntity> fetchHoldingsEntities =fetchBibliographicEntity.getHoldingsEntities();
            List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();

            logger.info("Owning Inst Bib Id :  = {}",bibliographicEntity.getOwningInstitutionBibId());
            logger.info("Fetched Item Entities = {}",fetchHoldingsEntities.size());
            logger.info("Incoming Item Entities = {}",holdingsEntities.size());

            for (Iterator iholdings = holdingsEntities.iterator(); iholdings.hasNext();) {
                HoldingsEntity holdingsEntity =(HoldingsEntity) iholdings.next();
                for (HoldingsEntity fetchHolding : fetchHoldingsEntities) {
                    if (fetchHolding.getOwningInstitutionHoldingsId().equalsIgnoreCase(holdingsEntity.getOwningInstitutionHoldingsId()) && fetchHolding.getOwningInstitutionId().intValue() == holdingsEntity.getOwningInstitutionId().intValue()) {
                        copyHoldingsEntity(fetchHolding, holdingsEntity);
                        iholdings.remove();
                    }else{
                        // Added for Boundwith scenarios
                        List<ItemEntity> fetchedItemEntityList = fetchHolding.getItemEntities();
                        List<ItemEntity> itemEntityList = holdingsEntity.getItemEntities();
                        if(CollectionUtils.isNotEmpty(itemEntityList)) {
                            List<ItemEntity> itemsToProcess = new ArrayList<>(itemEntityList);
                            for(ItemEntity fetchedItemEntity : fetchedItemEntityList){
                                for(ItemEntity itemEntity : itemsToProcess){
                                    if(fetchedItemEntity.getOwningInstitutionItemId().equals(itemEntity.getOwningInstitutionItemId())){
                                        copyHoldingsEntity(fetchHolding,holdingsEntity);
                                        iholdings.remove();
                                    }
                                }
                            }
                        }

                    }
                }
            }
            fetchHoldingsEntities.addAll(holdingsEntities);
            logger.info("Holding Final Count = {}",fetchHoldingsEntities.size());


            // Item
            List<ItemEntity> fetchItemsEntities =fetchBibliographicEntity.getItemEntities();
            List<ItemEntity> incomingItemsEntities = bibliographicEntity.getItemEntities();

            logger.info("Fetched Item Entities = {}",CollectionUtils.isNotEmpty(fetchItemsEntities) ? fetchItemsEntities.size() : 0);
            logger.info("Incoming Item Entities = {}",CollectionUtils.isNotEmpty(incomingItemsEntities) ? incomingItemsEntities.size() : 0);

            List<ItemEntity> finalItemEntities = new ArrayList<>();

            for (HoldingsEntity holdingsEntity : fetchHoldingsEntities) {
                finalItemEntities.addAll(holdingsEntity.getItemEntities());
            }

            logger.info("Item Final Count = {}",finalItemEntities.size());

            fetchBibliographicEntity.setHoldingsEntities(fetchHoldingsEntities);
            fetchBibliographicEntity.setItemEntities(finalItemEntities);

            savedBibliographicEntity = saveBibRecord(fetchBibliographicEntity);
        }
        return savedBibliographicEntity;
    }

    public BibliographicEntity saveBibRecord(BibliographicEntity fetchBibliographicEntity) {
        try {
            return accessionDAO.saveBibRecord(fetchBibliographicEntity);
        } catch (Exception e) {
            logger.info(RecapConstants.EXCEPTION,e);
        }
        return null;
    }

    private HoldingsEntity copyHoldingsEntity(HoldingsEntity fetchHoldingsEntity, HoldingsEntity holdingsEntity){
        fetchHoldingsEntity.setContent(holdingsEntity.getContent());
        fetchHoldingsEntity.setLastUpdatedBy(holdingsEntity.getLastUpdatedBy());
        fetchHoldingsEntity.setLastUpdatedDate(holdingsEntity.getLastUpdatedDate());
        fetchHoldingsEntity.setDeleted(holdingsEntity.isDeleted());
        List<ItemEntity> fetchedItemEntities = fetchHoldingsEntity.getItemEntities();

        processItems(fetchedItemEntities, holdingsEntity.getItemEntities());
        fetchHoldingsEntity.setItemEntities(fetchedItemEntities);

        return fetchHoldingsEntity;
    }

    private void processItems(List<ItemEntity> fetchItemsEntities, List<ItemEntity> itemsEntities) {
        for (Iterator iItems = itemsEntities.iterator(); iItems.hasNext();) {
            ItemEntity itemEntity =(ItemEntity) iItems.next();
            for (ItemEntity fetchItem : fetchItemsEntities) {
                if (fetchItem.getOwningInstitutionItemId().equalsIgnoreCase(itemEntity.getOwningInstitutionItemId()) && fetchItem.getOwningInstitutionId().intValue() == itemEntity.getOwningInstitutionId().intValue()) {
                    copyItemEntity(fetchItem, itemEntity);
                    iItems.remove();
                }
            }
        }
        fetchItemsEntities.addAll(itemsEntities);
    }

    private ItemEntity copyItemEntity(ItemEntity fetchItemEntity, ItemEntity itemEntity){
        fetchItemEntity.setBarcode(itemEntity.getBarcode());
        fetchItemEntity.setLastUpdatedBy(itemEntity.getLastUpdatedBy());
        fetchItemEntity.setLastUpdatedDate(itemEntity.getLastUpdatedDate());
        fetchItemEntity.setCallNumber(itemEntity.getCallNumber());
        fetchItemEntity.setCustomerCode(itemEntity.getCustomerCode());
        fetchItemEntity.setCallNumberType(itemEntity.getCallNumberType());
        fetchItemEntity.setItemAvailabilityStatusId(itemEntity.getItemAvailabilityStatusId());
        fetchItemEntity.setCopyNumber(itemEntity.getCopyNumber());
        fetchItemEntity.setCollectionGroupId(itemEntity.getCollectionGroupId());
        fetchItemEntity.setUseRestrictions(itemEntity.getUseRestrictions());
        fetchItemEntity.setVolumePartYear(itemEntity.getVolumePartYear());
        fetchItemEntity.setDeleted(itemEntity.isDeleted());
        fetchItemEntity.setCatalogingStatus(itemEntity.getCatalogingStatus());
        return fetchItemEntity;
    }

    /**
     * This method is used to re-accession the item for the item which is de-accessioned.
     * @param itemEntityList
     * @return
     */
    public String reAccessionItem(List<ItemEntity> itemEntityList){
        try {
            for(ItemEntity itemEntity:itemEntityList){
                itemEntity.setDeleted(false);
                Date currentDateTime = new Date();
                itemEntity.setLastUpdatedDate(currentDateTime);
                itemEntity.setLastUpdatedBy(RecapConstants.REACCESSION);

                for (HoldingsEntity holdingsEntity:itemEntity.getHoldingsEntities()) {
                    holdingsEntity.setDeleted(false);
                    holdingsEntity.setLastUpdatedDate(currentDateTime);
                    holdingsEntity.setLastUpdatedBy(RecapConstants.REACCESSION);
                }
                for(BibliographicEntity bibliographicEntity:itemEntity.getBibliographicEntities()) {
                    bibliographicEntity.setDeleted(false);
                    bibliographicEntity.setLastUpdatedDate(currentDateTime);
                    bibliographicEntity.setLastUpdatedBy(RecapConstants.REACCESSION);
                }
            }
            itemDetailsRepository.saveAll(itemEntityList);
            itemDetailsRepository.flush();
        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
            return RecapCommonConstants.FAILURE;
        }
        return RecapCommonConstants.SUCCESS;
    }

    /**
     * This method is used to index the re-accessioned item in solr.
     * @param itemEntityList
     * @return
     */
    public String indexReaccessionedItem(List<ItemEntity> itemEntityList){
        try {
            for(ItemEntity itemEntity:itemEntityList){
                List<BibliographicEntity>  bibliographicEntities = itemEntity.getBibliographicEntities();
                for (BibliographicEntity bibliographicEntity:bibliographicEntities) {
                    indexBibliographicRecord(bibliographicEntity.getBibliographicId());
                }
            }
        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
            return RecapCommonConstants.FAILURE;
        }
        return RecapCommonConstants.SUCCESS;
    }

    public void saveItemChangeLogEntity(String operationType, String message, List<ItemEntity> itemEntityList) {
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        for (ItemEntity itemEntity:itemEntityList) {
            ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
            itemChangeLogEntity.setOperationType(RecapCommonConstants.ACCESSION);
            itemChangeLogEntity.setUpdatedBy(operationType);
            itemChangeLogEntity.setUpdatedDate(new Date());
            itemChangeLogEntity.setRecordId(itemEntity.getItemId());
            itemChangeLogEntity.setNotes(message);
            itemChangeLogEntityList.add(itemChangeLogEntity);
        }
        itemChangeLogDetailsRepository.saveAll(itemChangeLogEntityList);
    }

    /**
     * This method is used to save the ReportEntity in the database.
     * @param owningInstitution
     * @param reportDataEntityList
     */
    @Transactional
    public void saveReportEntity(String owningInstitution, List<ReportDataEntity> reportDataEntityList) {
        ReportEntity reportEntity;
        reportEntity = getReportEntity(owningInstitution!=null ? owningInstitution : RecapConstants.UNKNOWN_INSTITUTION);
        reportEntity.setReportDataEntities(reportDataEntityList);
        producerTemplate.sendBody(RecapCommonConstants.REPORT_Q, reportEntity);
    }

    private ReportEntity getReportEntity(String owningInstitution){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapCommonConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ONGOING_ACCESSION_REPORT);
        reportEntity.setInstitutionName(owningInstitution);
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }

}
