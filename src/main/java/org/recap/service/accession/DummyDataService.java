package org.recap.service.accession;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.OwningInstitutionIDSequence;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.OwningInstitutionIDSequenceRepository;
import org.recap.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by premkb on 27/4/17.
 */
@Service
public class DummyDataService {

    private static final Logger logger = LoggerFactory.getLogger(DummyDataService.class);

    private Map<String,Integer> collectionGroupMap;

    private Map<String,Integer> itemStatusMap;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private OwningInstitutionIDSequenceRepository owningInstitutionIDSequenceRepository;

    @Autowired
    private AccessionDAO accessionDAO;

    @Autowired
    private CommonUtil commonUtil;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method is used to create dummy record when item barcode is not found in ILS.
     * @param owningInstitutionId the owning institution id
     * @param itemBarcode         the item barcode
     * @param customerCode        the customer code
     * @return the bibliographic entity
     */
    public BibliographicEntity createDummyDataAsIncomplete(Integer owningInstitutionId, String itemBarcode, String customerCode,ImsLocationEntity imsLocationEntity) {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        Date currentDate = new Date();
        try {
            updateBibWithDummyDetails(owningInstitutionId, bibliographicEntity, currentDate,RecapCommonConstants.ACCESSION, getDummyOwningInstId());

            HoldingsEntity holdingsEntity = getHoldingsWithDummyDetails(owningInstitutionId, currentDate,RecapCommonConstants.ACCESSION, getDummyOwningInstId());

            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setCallNumberType(RecapConstants.DUMMY_CALL_NUMBER_TYPE);
            itemEntity.setCallNumber(RecapCommonConstants.DUMMYCALLNUMBER);
            itemEntity.setCreatedDate(currentDate);
            itemEntity.setCreatedBy(RecapCommonConstants.ACCESSION);
            itemEntity.setLastUpdatedDate(currentDate);
            itemEntity.setLastUpdatedBy(RecapCommonConstants.ACCESSION);
            itemEntity.setBarcode(itemBarcode);
            itemEntity.setOwningInstitutionItemId(getDummyOwningInstId());
            itemEntity.setOwningInstitutionId(owningInstitutionId);
            itemEntity.setCollectionGroupId(getCollectionGroupMap().get(RecapCommonConstants.NOT_AVAILABLE_CGD));
            itemEntity.setCustomerCode(customerCode);
            itemEntity.setItemAvailabilityStatusId(getItemStatusMap().get(RecapCommonConstants.NOT_AVAILABLE));
            itemEntity.setDeleted(false);
            itemEntity.setHoldingsEntities(Collections.singletonList(holdingsEntity));
            itemEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
            itemEntity.setImsLocationId(imsLocationEntity.getImsLocationId());
            itemEntity.setImsLocationEntity(imsLocationEntity);
            List<ItemEntity> itemEntityList = new ArrayList<>();
            itemEntityList.add(itemEntity);
            holdingsEntity.setItemEntities(itemEntityList);

            bibliographicEntity.setHoldingsEntities(Collections.singletonList(holdingsEntity));
            bibliographicEntity.setItemEntities(Collections.singletonList(itemEntity));
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
        return accessionDAO.saveBibRecord(bibliographicEntity);
    }

    public HoldingsEntity getHoldingsWithDummyDetails(Integer owningInstitutionId, Date currentDate, String createdBy, String owningInstitutionHoldingsId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(getXmlContent(RecapConstants.DUMMY_HOLDING_CONTENT_XML).getBytes());
        holdingsEntity.setCreatedDate(currentDate);
        holdingsEntity.setCreatedBy(createdBy);
        holdingsEntity.setLastUpdatedDate(currentDate);
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);
        holdingsEntity.setLastUpdatedBy(createdBy);
        return holdingsEntity;
    }

    public void updateBibWithDummyDetails(Integer owningInstitutionId, BibliographicEntity bibliographicEntity, Date currentDate,
                                          String createdBy, String owningInstitutionBibId
    ) {
        bibliographicEntity.setContent(getXmlContent(RecapConstants.DUMMY_BIB_CONTENT_XML).getBytes());
        bibliographicEntity.setCreatedDate(currentDate);
        bibliographicEntity.setCreatedBy(createdBy);
        bibliographicEntity.setLastUpdatedBy(createdBy);
        bibliographicEntity.setLastUpdatedDate(currentDate);
        bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
    }

    private synchronized Map<String, Integer> getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap<>();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
                for (Iterator<CollectionGroupEntity> iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                    CollectionGroupEntity collectionGroupEntity =  iterator.next();
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return collectionGroupMap;
    }

    private synchronized Map<String, Integer> getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap<>();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
                for (ItemStatusEntity itemStatusEntity : itemStatusEntities) {
                    itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return itemStatusMap;
    }

    private String getXmlContent(String filename) {
        return commonUtil.getContentByFileName(filename).toString();
    }

    private String getDummyOwningInstId(){
        OwningInstitutionIDSequence owningInstitutionIDSequence = new OwningInstitutionIDSequence();
        OwningInstitutionIDSequence savedOwningInstitutionIDSequence = owningInstitutionIDSequenceRepository.saveAndFlush(owningInstitutionIDSequence);
        logger.info("seq id---->{}",savedOwningInstitutionIDSequence.getID());
        return "d"+savedOwningInstitutionIDSequence.getID();
    }
}
