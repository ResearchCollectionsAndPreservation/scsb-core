package org.recap.service;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 05/Mar/2021
 */
@Repository
public class BibliographicRepositoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    /**
     * This method can save a bibliographic/holdings/item entity if it is new or update if it already exists
     * @param bibliographicEntity BibliographicEntity
     * @return BibliographicEntity
     */
    @Transactional
    public BibliographicEntity saveOrUpdate(BibliographicEntity bibliographicEntity) {
        boolean isNew = true;
        isNew = fetchBibliographicEntityAndSetIdIfExists(bibliographicEntity, isNew);
        isNew = fetchHoldingEntitiesAndSetIdIfExists(bibliographicEntity, isNew);
        isNew = fetchItemEntitiesAndSetIdIfExists(bibliographicEntity, isNew);
        BibliographicEntity savedBibliographicEntity = null;
        if (isNew) {
            savedBibliographicEntity = bibliographicDetailsRepository.save(bibliographicEntity);
        } else {
            savedBibliographicEntity = entityManager.merge(bibliographicEntity);
        }
        entityManager.flush();
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    @Transactional
    public List<BibliographicEntity> saveOrUpdateList(List<BibliographicEntity> bibliographicEntityList) {
        List<BibliographicEntity> savedBibliographicEntityList = new ArrayList<>();
        for(BibliographicEntity bibliographicEntity : bibliographicEntityList) {
            savedBibliographicEntityList.add(saveOrUpdate(bibliographicEntity));
        }
        entityManager.clear();
        return savedBibliographicEntityList;
    }

    private boolean fetchBibliographicEntityAndSetIdIfExists(BibliographicEntity bibliographicEntity, boolean isNew) {
        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId());
        if (null != fetchedBibliographicEntity) {
            isNew = false;
            bibliographicEntity.setId(fetchedBibliographicEntity.getId());
        }
        return isNew;
    }

    private boolean fetchHoldingEntitiesAndSetIdIfExists(BibliographicEntity bibliographicEntity, boolean isNew) {
        for (HoldingsEntity holdingsEntity : bibliographicEntity.getHoldingsEntities()) {
            HoldingsEntity fetchedHoldingsEntity = holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(holdingsEntity.getOwningInstitutionHoldingsId(), holdingsEntity.getOwningInstitutionId());
            if (null != fetchedHoldingsEntity) {
                isNew = false;
                holdingsEntity.setId(fetchedHoldingsEntity.getId());
            }
            for (ItemEntity itemEntity : holdingsEntity.getItemEntities()) {
                ItemEntity fetchedItemEntity = itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(itemEntity.getOwningInstitutionItemId(), itemEntity.getOwningInstitutionId());
                if (null != fetchedItemEntity) {
                    isNew = false;
                    itemEntity.setId(fetchedItemEntity.getId());
                }
            }
            if (null != holdingsEntity.getBibliographicEntities()) {
                for (BibliographicEntity bibliographicEntityFromHoldings : holdingsEntity.getBibliographicEntities()) {
                    isNew = fetchBibliographicEntityAndSetIdIfExists(bibliographicEntityFromHoldings, isNew);
                }
            }
        }
        return isNew;
    }

    private boolean fetchItemEntitiesAndSetIdIfExists(BibliographicEntity bibliographicEntity, boolean isNew) {
        if (null != bibliographicEntity.getItemEntities()) {
            for (ItemEntity itemEntity : bibliographicEntity.getItemEntities()) {
                ItemEntity fetchedItemEntity = itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(itemEntity.getOwningInstitutionItemId(), itemEntity.getOwningInstitutionId());
                if (null != fetchedItemEntity) {
                    isNew = false;
                    itemEntity.setId(fetchedItemEntity.getId());
                }
                if (null != itemEntity.getBibliographicEntities()) {
                    for (BibliographicEntity bibliographicEntityFromItem : itemEntity.getBibliographicEntities()) {
                        isNew = fetchBibliographicEntityAndSetIdIfExists(bibliographicEntityFromItem, isNew);
                    }
                }
            }
        }
        return isNew;
    }
}
