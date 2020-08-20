package org.recap.service.common;

import org.recap.RecapCommonConstants;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by premkb on 11/6/17.
 */
@Service
public class SetupDataService {

    private static final Logger logger = LoggerFactory.getLogger(SetupDataService.class);

    @Autowired
    private RepositoryService repositoryService;

    private Map<Integer,String> itemStatusIdCodeMap;

    private Map<String,Integer> itemStatusCodeIdMap;

    private Map<Integer,String> institutionEntityMap;

    private Map<String,Integer> institutionCodeIdMap;

    private Map<String,Integer> collectionGroupMap;

    /**
     * Gets item status id and item status code from db and puts it into a map where status id as key and status code as value.
     *
     * @return the item status id code map
     */
    public Map getItemStatusIdCodeMap() {
        if (null == itemStatusIdCodeMap) {
            itemStatusIdCodeMap = new HashMap();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = repositoryService.getItemStatusDetailsRepository().findAll();
                for (ItemStatusEntity itemStatusEntity : itemStatusEntities) {
                    itemStatusIdCodeMap.put(itemStatusEntity.getId(), itemStatusEntity.getStatusCode());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return itemStatusIdCodeMap;
    }

    /**
     * Gets item status code and item status id from db and puts it into a map where status code as key and status id as value.
     *
     * @return the item status code id map
     */
    public Map getItemStatusCodeIdMap() {
        if (null == itemStatusCodeIdMap) {
            itemStatusCodeIdMap = new HashMap();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = repositoryService.getItemStatusDetailsRepository().findAll();
                for (ItemStatusEntity itemStatusEntity : itemStatusEntities) {
                    itemStatusCodeIdMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return itemStatusCodeIdMap;
    }

    /**
     * Gets institution id and institution code from db and puts it into a map where status id as key and status code as value.
     *
     * @return the institution entity map
     */
    public Map getInstitutionIdCodeMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap();
            try {
                Iterable<InstitutionEntity> institutionEntities = repositoryService.getInstitutionDetailsRepository().findAll();
                for (InstitutionEntity institutionEntity : institutionEntities) {
                    institutionEntityMap.put(institutionEntity.getId(), institutionEntity.getInstitutionCode());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return institutionEntityMap;
    }

    public Map getInstitutionCodeIdMap() {
        if (null == institutionCodeIdMap) {
            institutionCodeIdMap = new HashMap();
            try {
                Iterable<InstitutionEntity> institutionEntities = repositoryService.getInstitutionDetailsRepository().findAll();
                for (InstitutionEntity institutionEntity : institutionEntities) {
                    institutionCodeIdMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return institutionCodeIdMap;
    }

    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = repositoryService.getCollectionGroupDetailsRepository().findAll();
                for (CollectionGroupEntity collectionGroupEntity : collectionGroupEntities) {
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getId());
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return collectionGroupMap;
    }
}
