package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.*;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.repository.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CommonUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private Map itemStatusMap;
    private Map collectionGroupMap;
    private Map institutionEntityMap;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    MarcUtil marcUtil;

    @Autowired
    AccessionUtil accessionUtil;

    /**
     * This method builds Holdings Entity from holdings content
     * @param bibliographicEntity
     * @param currentDate
     * @param errorMessage
     * @param holdingsContent
     * @return
     */
    public HoldingsEntity buildHoldingsEntity(BibliographicEntity bibliographicEntity, Date currentDate, StringBuilder errorMessage, String holdingsContent) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        if (StringUtils.isNotBlank(holdingsContent)) {
            holdingsEntity.setContent(holdingsContent.getBytes());
        } else {
            errorMessage.append(" Holdings Content cannot be empty");
        }
        holdingsEntity.setCreatedDate(currentDate);
        holdingsEntity.setCreatedBy(RecapConstants.SUBMIT_COLLECTION);
        holdingsEntity.setLastUpdatedDate(currentDate);
        holdingsEntity.setLastUpdatedBy(RecapConstants.SUBMIT_COLLECTION);
        Integer owningInstitutionId = bibliographicEntity.getOwningInstitutionId();
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        return holdingsEntity;
    }

    public void buildSubmitCollectionReportInfoAndAddFailures(BibliographicEntity fetchedBibliographicEntity, List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList, String owningInstitution, Map.Entry<String, Map<String, ItemEntity>> incomingHoldingItemMapEntry, ItemEntity incomingItemEntity) {
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setItemBarcode(incomingItemEntity.getBarcode());
        submitCollectionReportInfo.setCustomerCode(incomingItemEntity.getCustomerCode());
        submitCollectionReportInfo.setOwningInstitution(owningInstitution);
        String existingOwningInstitutionHoldingsId = getExistingItemEntityOwningInstItemId(fetchedBibliographicEntity,incomingItemEntity);
        submitCollectionReportInfo.setMessage(RecapConstants.SUBMIT_COLLECTION_FAILED_RECORD+" - Owning institution holdings id mismatch - incoming owning institution holdings id " +incomingHoldingItemMapEntry.getKey()+ ", existing owning institution item id "+incomingItemEntity.getOwningInstitutionItemId()
                +", existing owning institution holdings id "+existingOwningInstitutionHoldingsId+", existing owning institution bib id "+fetchedBibliographicEntity.getOwningInstitutionBibId());
        failureSubmitCollectionReportInfoList.add(submitCollectionReportInfo);
    }

    public void buildSubmitCollectionReportInfoWhenNoGroupIdAndAddFailures(BibliographicEntity incomingBibliographicEntity, List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList, String owningInstitution, ItemEntity incomingItemEntity) {
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setItemBarcode(incomingItemEntity.getBarcode());
        submitCollectionReportInfo.setCustomerCode(incomingItemEntity.getCustomerCode());
        submitCollectionReportInfo.setOwningInstitution(owningInstitution);
        submitCollectionReportInfo.setMessage(RecapConstants.SUBMIT_COLLECTION_FAILED_RECORD+" - "+"Unable to update dummy record, CGD is unavailable in the incoming xml record - incoming owning institution bib id - "+incomingBibliographicEntity.getOwningInstitutionBibId()
                +", incoming owning institution item id - "+incomingItemEntity.getOwningInstitutionItemId());
        failureSubmitCollectionReportInfoList.add(submitCollectionReportInfo);
    }

    private String getExistingItemEntityOwningInstItemId(BibliographicEntity fetchedBibliographicEntity,ItemEntity incomingItemEntity){
        for(ItemEntity fetchedItemEntity:fetchedBibliographicEntity.getItemEntities()){
            if(fetchedItemEntity.getOwningInstitutionItemId().equals(incomingItemEntity.getOwningInstitutionItemId())){
                return fetchedItemEntity.getHoldingsEntities().get(0).getOwningInstitutionHoldingsId();
            }
        }
        return "";
    }

    public void addItemAndReportEntities(List<ItemEntity> itemEntities, List<ReportEntity> reportEntities, boolean processHoldings, HoldingsEntity holdingsEntity, Map<String, Object> itemMap) {
        ItemEntity itemEntity = (ItemEntity) itemMap.get("itemEntity");
        ReportEntity itemReportEntity = (ReportEntity) itemMap.get("itemReportEntity");
        if (itemReportEntity != null) {
            reportEntities.add(itemReportEntity);
        } else if (processHoldings) {
            if (holdingsEntity.getItemEntities() == null) {
                holdingsEntity.setItemEntities(new ArrayList<>());
            }
            holdingsEntity.getItemEntities().add(itemEntity);
            itemEntities.add(itemEntity);
        }
    }

    /**
     * Add Holdings Entity to Map
     * @param map
     * @param holdingsEntity
     * @param owningInstitutionHoldingsId
     * @return
     */
    public Map<String, Object> addHoldingsEntityToMap(Map<String, Object> map, HoldingsEntity holdingsEntity, String owningInstitutionHoldingsId) {
        if (StringUtils.isBlank(owningInstitutionHoldingsId) || owningInstitutionHoldingsId.length() > 100) {
            owningInstitutionHoldingsId = UUID.randomUUID().toString();
        }
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);
        map.put("holdingsEntity", holdingsEntity);
        return map;
    }

    /**
     * Gets item status map.
     *
     * @return the item status map
     */
    public Map getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
                for (Iterator iterator = itemStatusEntities.iterator(); iterator.hasNext(); ) {
                    ItemStatusEntity itemStatusEntity = (ItemStatusEntity) iterator.next();
                    itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return itemStatusMap;
    }

    /**
     * Gets collection group map.
     *
     * @return the collection group map
     */
    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
                for (Iterator iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                    CollectionGroupEntity collectionGroupEntity = (CollectionGroupEntity) iterator.next();
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return collectionGroupMap;
    }

    /**
     * Gets institution entity map.
     *
     * @return the institution entity map
     */
    public Map getInstitutionEntityMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap();
            try {
                Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
                for (Iterator iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                    InstitutionEntity institutionEntity = (InstitutionEntity) iterator.next();
                    institutionEntityMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return institutionEntityMap;
    }

    /**
     * Rollback update item availabiluty status.
     *
     * @param itemEntity the item entity
     * @param userName   the user name
     */
    public void rollbackUpdateItemAvailabilutyStatus(ItemEntity itemEntity, String userName) {
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.AVAILABLE);
        itemEntity.setItemAvailabilityStatusId(itemStatusEntity.getId()); // Available
        itemEntity.setLastUpdatedBy(getUser(userName));
        itemDetailsRepository.save(itemEntity);
        saveItemChangeLogEntity(itemEntity.getItemId(), getUser(userName), RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_UPDATE, RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_ROLLBACK);
    }

    /**
     * Save item change log entity.
     *
     * @param recordId      the record id
     * @param userName      the user name
     * @param operationType the operation type
     * @param notes         the notes
     */
    public void saveItemChangeLogEntity(Integer recordId, String userName, String operationType, String notes) {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setUpdatedBy(userName);
        itemChangeLogEntity.setUpdatedDate(new Date());
        itemChangeLogEntity.setOperationType(operationType);
        itemChangeLogEntity.setRecordId(recordId);
        itemChangeLogEntity.setNotes(notes);
        itemChangeLogDetailsRepository.save(itemChangeLogEntity);
    }

    /**
     * Gets user.
     *
     * @param userId the user id
     * @return the user
     */
    public String getUser(String userId) {
        if (StringUtils.isBlank(userId)) {
            return "Discovery";
        } else {
            return userId;
        }
    }

    /**
     * This method gets input string where the input will be in  SCSB xml format, unmarshals and maps to BibRecords and then returns BibRecords
     * @param unmarshal - this the input xml which will be in SCSB xml format
     * @return BibRecords
     */
    public BibRecords getBibRecordsForSCSBFormat(String unmarshal) {
        BibRecords bibRecords = null;
        try {
            extractBibRecords(unmarshal);
        } catch (JAXBException e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
        return bibRecords;
    }

    public Object marcRecordConvert(String bibDataResponse) {
        List<Record> records = new ArrayList<>();
        if (StringUtils.isNotBlank(bibDataResponse)) {
            records = marcUtil.readMarcXml(bibDataResponse);
        }
        return records;
    }

    public BibRecords extractBibRecords(String inputRecords) throws JAXBException {
        BibRecords bibRecords;
        JAXBContext context = JAXBContext.newInstance(BibRecords.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        InputStream stream = new ByteArrayInputStream(inputRecords.getBytes(StandardCharsets.UTF_8));
        XMLStreamReader xsr = null;
        try {
            xsr = xif.createXMLStreamReader(stream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        Unmarshaller um = context.createUnmarshaller();
        bibRecords = (BibRecords) um.unmarshal(xsr);
        logger.info("bibrecord size {}", bibRecords.getBibRecordList().size());
        return bibRecords;
    }

    public String getUpdatedDataResponse(Set<AccessionResponse> accessionResponsesList, List<Map<String, String>> responseMapList, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest, boolean isValidBoundWithRecord, int count, Object record, ImsLocationEntity imsLocationEntity) {
        String response;
        boolean isFirstRecord = false;
        if (count == 1) {
            isFirstRecord = true;
        }
        response = accessionUtil.updateData(record, owningInstitution, responseMapList, accessionRequest, isValidBoundWithRecord, isFirstRecord,imsLocationEntity);
        accessionUtil.setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), response);
        reportDataEntityList.addAll(accessionUtil.createReportDataEntityList(accessionRequest, response));
        return response;
    }

    public StringBuilder getContentByFileName(String vmFileName) {
        InputStream inputStream = getClass().getResourceAsStream(vmFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    out.append(line);
                }
                out.append("\n");
            }
        } catch (IOException e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
        return out;
    }

    /**
     * Get list of barcodes from item entities
     * @param itemEntities
     * @return
     */
    public List<String> getBarcodesList(List<ItemEntity> itemEntities) {
        List<String> itemBarcodes = new ArrayList<>();
        if (!itemEntities.isEmpty()) {
            for (ItemEntity itemEntity : itemEntities) {
                itemBarcodes.add(itemEntity.getBarcode());
            }
        }
        return itemBarcodes;
    }
}
