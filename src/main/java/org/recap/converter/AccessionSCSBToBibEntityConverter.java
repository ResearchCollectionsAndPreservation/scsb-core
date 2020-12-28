package org.recap.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.Items;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.LeaderFieldType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.util.CommonUtil;
import org.recap.util.DBReportUtil;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccessionSCSBToBibEntityConverter implements AccessionXmlToBibEntityConverterInterface {
    private static final Logger logger = LoggerFactory.getLogger(AccessionSCSBToBibEntityConverter.class);

    @Autowired
    private DBReportUtil dbReportUtil;

    @Autowired
    private MarcUtil marcUtil;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    /**
     * This method is used to convert scsb record into bib entity
     * @param scsbRecord
     * @param institutionName the institution name
     * @param accessionRequest    the customer code
     * @return
     */
    @Override
    public Map convert(Object scsbRecord, String institutionName, AccessionRequest accessionRequest, ImsLocationEntity imsLocationEntity){
        int failedItemCount = 0;
        int successItemCount = 0;
        String reasonForFailureItem = "";
        Map<String, Object> map = new HashMap<>();
        String incompleteResponse = "";
        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        List<ItemEntity> itemEntities = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();

        getDbReportUtil().setInstitutionEntitiesMap(commonUtil.getInstitutionEntityMap());
        getDbReportUtil().setCollectionGroupMap(commonUtil.getCollectionGroupMap());

        try {
            BibRecord bibRecord = (BibRecord) scsbRecord;
            Integer owningInstitutionId = commonUtil.getInstitutionEntityMap().get(institutionName);
            Date currentDate = new Date();

            Map<String, Object> bibMap = processAndValidateBibliographicEntity(bibRecord, owningInstitutionId, currentDate,errorMessage);
            BibliographicEntity bibliographicEntity = (BibliographicEntity) bibMap.get("bibliographicEntity");

            map.put(RecapCommonConstants.FAILED_BIB_COUNT, bibMap.get(RecapCommonConstants.FAILED_BIB_COUNT));
            map.put(RecapCommonConstants.SUCCESS_BIB_COUNT , bibMap.get(RecapCommonConstants.SUCCESS_BIB_COUNT));
            map.put(RecapCommonConstants.REASON_FOR_BIB_FAILURE , bibMap.get(RecapCommonConstants.REASON_FOR_BIB_FAILURE));
            map.put(RecapCommonConstants.EXIST_BIB_COUNT , bibMap.get(RecapCommonConstants.EXIST_BIB_COUNT));

            List<Holdings> holdingsList = bibRecord.getHoldings();
            if (errorMessage.length()==0) {
                for(Holdings holdings:holdingsList){
                    List<Holding> holdingList=null;
                    if (holdings.getHolding()!=null) {
                        holdingList = holdings.getHolding();
                    } else {
                        logger.error(String.format("holding is empty---%s",bibRecord.getBib().getOwningInstitutionBibId()));
                    }
                    for(Holding holding:holdingList){
                        if (holding.getContent() != null) {
                            CollectionType holdingContentCollection = holding.getContent().getCollection();
                            List<RecordType> holdingRecordTypes = holdingContentCollection.getRecord();
                            RecordType holdingsRecordType = holdingRecordTypes.get(0);

                            Map<String, Object> holdingsMap = processAndValidateHoldingsEntity(bibliographicEntity, holding, holdingContentCollection,currentDate,errorMessage);
                            HoldingsEntity holdingsEntity = (HoldingsEntity) holdingsMap.get("holdingsEntity");
                            if (errorMessage.length()==0) {
                                holdingsEntities.add(holdingsEntity);
                            }
                            String holdingsCallNumber = getMarcUtil().getDataFieldValueForRecordType(holdingsRecordType, "852", null, null, "h");
                            if(holdingsCallNumber == null){
                                holdingsCallNumber = "";
                            }
                            String holdingsCallNumberType = getMarcUtil().getInd1ForRecordType(holdingsRecordType, "852", "h");

                            List<Items> items = holding.getItems();
                            for (Items item : items) {
                                ContentType itemContent = item.getContent();
                                CollectionType itemContentCollection = itemContent.getCollection();

                                List<RecordType> itemRecordTypes = itemContentCollection.getRecord();
                                for (RecordType itemRecordType : itemRecordTypes) {

                                    Map<String, Object> itemMap = processAndValidateItemEntity(bibliographicEntity, owningInstitutionId, holdingsCallNumber, holdingsCallNumberType, itemRecordType,accessionRequest,currentDate,errorMessage,imsLocationEntity);

                                    if (itemMap != null) {
                                        if(itemMap.containsKey(RecapCommonConstants.FAILED_ITEM_COUNT)){
                                            failedItemCount = failedItemCount + (int) itemMap.get(RecapCommonConstants.FAILED_ITEM_COUNT);
                                        }
                                        if(itemMap.containsKey(RecapCommonConstants.ITEMBARCODE)){
                                            map.put(RecapCommonConstants.ITEMBARCODE, itemMap.get(RecapCommonConstants.ITEMBARCODE));
                                        }
                                        if(itemMap.containsKey(RecapCommonConstants.REASON_FOR_ITEM_FAILURE)){
                                            String reason = (String)itemMap.get(RecapCommonConstants.REASON_FOR_ITEM_FAILURE);
                                            if(!StringUtils.isEmpty(reason)){
                                                if(StringUtils.isEmpty(reasonForFailureItem)){
                                                    reasonForFailureItem = (String) itemMap.get(RecapCommonConstants.REASON_FOR_ITEM_FAILURE);
                                                }else{
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    stringBuilder.append(itemMap.get(RecapCommonConstants.REASON_FOR_ITEM_FAILURE));
                                                    stringBuilder.append(",");
                                                    stringBuilder.append(reasonForFailureItem);
                                                    reasonForFailureItem = stringBuilder.toString();
                                                }

                                            }
                                        }
                                        if(itemMap.containsKey(RecapCommonConstants.SUCCESS_ITEM_COUNT)){
                                            successItemCount = successItemCount + (int) itemMap.get(RecapCommonConstants.SUCCESS_ITEM_COUNT);
                                        }
                                        ItemEntity itemEntity = (ItemEntity) itemMap.get("itemEntity");
                                        if (errorMessage.length()==0) {
                                            if (holdingsEntity.getItemEntities() == null) {
                                                holdingsEntity.setItemEntities(new ArrayList<>());
                                            }
                                            holdingsEntity.getItemEntities().add(itemEntity);
                                            itemEntities.add(itemEntity);
                                        }
                                        if(RecapCommonConstants.INCOMPLETE_STATUS.equalsIgnoreCase(itemEntity.getCatalogingStatus())){
                                            incompleteResponse = RecapCommonConstants.INCOMPLETE_STATUS;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    bibliographicEntity.setHoldingsEntities(holdingsEntities);
                    bibliographicEntity.setItemEntities(itemEntities);
                }
            }
            if (errorMessage.length()==0) {
                map.put(RecapCommonConstants.BIBLIOGRAPHICENTITY, bibliographicEntity);
            }
            map.put(RecapCommonConstants.FAILED_ITEM_COUNT,failedItemCount);
            map.put(RecapCommonConstants.SUCCESS_ITEM_COUNT,successItemCount);
            map.put(RecapCommonConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
            map.put(RecapConstants.INCOMPLETE_RESPONSE,incompleteResponse);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
            errorMessage.append(e.getMessage());
        }

        if(errorMessage.length()>0) {//Added to remove "," from the error message
            errorMessage = new StringBuilder(errorMessage.substring(0,errorMessage.length()-1));
        }
        map.put("errorMessage",errorMessage);
        return map;
    }

    /**
     * This method is used to validate all necessary bib record fields
     * @param bibRecord
     * @param owningInstitutionId
     * @param currentDate
     * @return
     */
    private Map<String, Object> processAndValidateBibliographicEntity(BibRecord bibRecord,Integer owningInstitutionId,Date currentDate,StringBuilder errorMessage) {
        int failedBibCount = 0;
        int successBibCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        Map<String, Object> map = new HashMap<>();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        String owningInstitutionBibId = bibRecord.getBib().getOwningInstitutionBibId();

        if (StringUtils.isNotBlank(owningInstitutionBibId)) {
            bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        } else {
            errorMessage.append("Owning Institution Bib Id cannot be null").append(",");
        }
        if (owningInstitutionId != null) {
            bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        } else {
            errorMessage.append(" Owning Institution Id cannot be null ").append(",");
        }
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCreatedDate(currentDate);
        bibliographicEntity.setCreatedBy(RecapCommonConstants.ACCESSION);
        bibliographicEntity.setLastUpdatedDate(currentDate);
        bibliographicEntity.setLastUpdatedBy(RecapCommonConstants.ACCESSION);

        ContentType bibContent = bibRecord.getBib().getContent();
        CollectionType bibContentCollection = bibContent.getCollection();
        String bibXmlContent = bibContentCollection.serialize(bibContentCollection);
        if (!StringUtils.isEmpty(bibXmlContent)) {
            bibliographicEntity.setContent(bibXmlContent.getBytes());
        } else {
            errorMessage.append(" Bib Content cannot be empty").append(",");
        }

        boolean subFieldExistsFor245 = getMarcUtil().isSubFieldExists(bibContentCollection.getRecord().get(0), "245");

        if (!subFieldExistsFor245) {
            errorMessage.append(" At least one subfield should be there for 245 tag").append(",");
        }

        LeaderFieldType leader = bibContentCollection.getRecord().get(0).getLeader();
        if(leader == null){
            errorMessage.append(" Leader field is missing").append(",");
        } else if (!(StringUtils.isNotBlank(leader.getValue()) && leader.getValue().length() == 24)) {
            errorMessage.append(" Leader field value should be 24 characters ").append(",");
        }

        if(owningInstitutionId != null && StringUtils.isNotBlank(owningInstitutionBibId)){
            BibliographicEntity existBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(owningInstitutionId,owningInstitutionBibId);
            if(null != existBibliographicEntity){
                exitsBibCount = 1;
            }
        }
        if(errorMessage.toString().length()==0){
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
     * This method is used to validate all necessary holdings fields required in the bib record.
     * @param bibliographicEntity
     * @param holding
     * @param holdingContentCollection
     * @param currentDate
     * @param errorMessage
     * @return
     */
    private Map<String, Object> processAndValidateHoldingsEntity(BibliographicEntity bibliographicEntity, Holding holding, CollectionType holdingContentCollection,Date currentDate,StringBuilder errorMessage) {
        Map<String, Object> map = new HashMap<>();
        String holdingsContent = holdingContentCollection.serialize(holdingContentCollection);
        HoldingsEntity holdingsEntity = commonUtil.buildHoldingsEntity(bibliographicEntity, currentDate, errorMessage, holdingsContent);
        String owningInstitutionHoldingsId = holding.getOwningInstitutionHoldingsId();
        return commonUtil.addHoldingsEntityToMap(map, holdingsEntity, owningInstitutionHoldingsId);
    }

    /**
     * This method is used to validate all necessary fields required for item.
     *
     * @param bibliographicEntity
     * @param owningInstitutionId
     * @param holdingsCallNumber
     * @param holdingsCallNumberType
     * @param itemRecordType
     * @param accessionRequest
     * @param currentDate
     * @param errorMessage
     * @return
     */
    private Map<String, Object> processAndValidateItemEntity(BibliographicEntity bibliographicEntity, Integer owningInstitutionId,
                                                             String holdingsCallNumber, String holdingsCallNumberType, RecordType itemRecordType,AccessionRequest accessionRequest,
                                                             Date currentDate,StringBuilder errorMessage,ImsLocationEntity imsLocationEntity) {

        Map<String, Object> map = new HashMap<>();
        ItemEntity itemEntity = new ItemEntity();
        int failedItemCount = 0;
        int successItemCount = 0;
        boolean isComplete = true;
        String reasonForFailureItem = "";
        map.put(RecapCommonConstants.FAILED_ITEM_COUNT,failedItemCount);
        map.put(RecapCommonConstants.SUCCESS_ITEM_COUNT,successItemCount);
        map.put(RecapCommonConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
        String itemBarcode = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "p");
        if (accessionRequest.getItemBarcode().equals(itemBarcode)) {//This is to avoid creation of multiple items when response from partner service is having 1Bib 1Hold n items, accession should be done for one item which comes in the request and should not be done for other items which is linked with the same bib
            if (StringUtils.isNotBlank(itemBarcode)) {
                itemEntity.setBarcode(itemBarcode);
                map.put("itemBarcode",itemBarcode);
            } else {
                errorMessage.append("Item Barcode cannot be null").append(",");
            }
            itemEntity.setCustomerCode(accessionRequest.getCustomerCode());
            itemEntity.setCallNumber(holdingsCallNumber);
            itemEntity.setCallNumberType(holdingsCallNumberType != null ? holdingsCallNumberType : "");
            String copyNumber = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "t");
            if (StringUtils.isNotBlank(copyNumber) && NumberUtils.isCreatable(copyNumber)) {
                itemEntity.setCopyNumber(Integer.valueOf(copyNumber));
            }
            if (owningInstitutionId != null) {
                itemEntity.setOwningInstitutionId(owningInstitutionId);
            } else {
                errorMessage.append("\n");
                errorMessage.append("Owning Institution Id cannot be null").append(",");
            }
            String collectionGroupCode = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "900", null, null, "a");
            if (StringUtils.isNotBlank(collectionGroupCode) && commonUtil.getCollectionGroupMap().containsKey(collectionGroupCode)) {
                itemEntity.setCollectionGroupId(commonUtil.getCollectionGroupMap().get(collectionGroupCode));
            } else {
                itemEntity.setCollectionGroupId(commonUtil.getCollectionGroupMap().get("Open"));
            }
            itemEntity.setDeleted(false);
            itemEntity.setCreatedDate(currentDate);
            itemEntity.setCreatedBy(RecapCommonConstants.ACCESSION);
            itemEntity.setLastUpdatedDate(currentDate);
            itemEntity.setLastUpdatedBy(RecapCommonConstants.ACCESSION);

            itemEntity.setImsLocationEntity(imsLocationEntity);
            itemEntity.setImsLocationId(imsLocationEntity.getImsLocationId());

            String useRestrictions = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "h");
            if (StringUtils.isNotBlank(useRestrictions) && ("In Library Use".equalsIgnoreCase(useRestrictions) || "Supervised Use".equalsIgnoreCase(useRestrictions))) {
                itemEntity.setUseRestrictions(useRestrictions);
            } else if(null == useRestrictions){
                isComplete = false;
            }

            itemEntity.setVolumePartYear(getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "3"));
            String owningInstitutionItemId = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "a");
            if (StringUtils.isNotBlank(owningInstitutionItemId)) {
                itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
            } else {
                errorMessage.append("Item Owning Institution Id cannot be null").append(",");
            }

            if(isComplete){
                itemEntity.setItemAvailabilityStatusId(commonUtil.getItemStatusMap().get("Available"));
                bibliographicEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
                itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
            } else {
                itemEntity.setItemAvailabilityStatusId(commonUtil.getItemStatusMap().get("Not Available"));
                bibliographicEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
                itemEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
            }
            if (errorMessage.toString().length() > 1) {
                if(map.containsKey(RecapCommonConstants.FAILED_ITEM_COUNT)){
                    failedItemCount = ((int) map.get(RecapCommonConstants.FAILED_ITEM_COUNT)) + 1;
                    map.put(RecapCommonConstants.FAILED_ITEM_COUNT,failedItemCount);
                }
                if(map.containsKey(RecapCommonConstants.REASON_FOR_ITEM_FAILURE)){
                    reasonForFailureItem = errorMessage.toString();
                    map.put(RecapCommonConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
                }
            }else{
                if(map.containsKey(RecapCommonConstants.SUCCESS_ITEM_COUNT)){
                    successItemCount = (int) map.get(RecapCommonConstants.SUCCESS_ITEM_COUNT) + 1;
                    map.put(RecapCommonConstants.SUCCESS_ITEM_COUNT,successItemCount);
                }
            }
            map.put("itemEntity", itemEntity);
            return map;
        }
        return null;
    }

    /**
     * This method gets db report util.
     *
     * @return the db report util
     */
    public DBReportUtil getDbReportUtil() {
        return dbReportUtil;
    }

    /**
     * This method sets db report util.
     *
     * @param dbReportUtil the db report util
     */
    public void setDbReportUtil(DBReportUtil dbReportUtil) {
        this.dbReportUtil = dbReportUtil;
    }

    /**
     * Gets marc util.
     *
     * @return the marc util
     */
    public MarcUtil getMarcUtil() {
        if (null == marcUtil) {
            marcUtil = new MarcUtil();
        }
        return marcUtil;
    }
}
