package org.recap.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by rajeshbabuk on 05/Mar/2021
 */
public class BibliographicRepositoryDAOUT extends BaseTestCaseUT {

    @InjectMocks
    BibliographicRepositoryDAO bibliographicRepositoryDAO;

    @Mock
    EntityManager entityManager;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    HoldingsEntity holdingsEntity;

    @Mock
    ItemEntity itemEntity;

    @Test
    public void saveOrUpdate() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("11");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("22");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setOwningInstitutionItemId("33");
        holdingsEntity.setItemEntities(Collections.singletonList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Collections.singletonList(holdingsEntity));
        bibliographicEntity.setItemEntities(Collections.singletonList(itemEntity));
        Mockito.when(bibliographicDetailsRepository.save(Mockito.any())).thenReturn(bibliographicEntity);
        Mockito.doNothing().when(entityManager).refresh(Mockito.any());
        BibliographicEntity savedBibliographicEntity = bibliographicRepositoryDAO.saveOrUpdate(bibliographicEntity);
        assertNotNull(savedBibliographicEntity);
    }

    @Test
    public void saveOrUpdateExisting() {
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(holdingsEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(itemEntity.getOwningInstitutionItemId()).thenReturn("333");
        Mockito.when(itemEntity.getOwningInstitutionId()).thenReturn(3);
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(Mockito.anyString(), Mockito.anyInt())).thenReturn(itemEntity);
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(bibliographicEntity.getItemEntities()).thenReturn(itemEntities);
        Mockito.when(holdingsEntity.getItemEntities()).thenReturn(itemEntities);
        Mockito.when(holdingsEntity.getOwningInstitutionId()).thenReturn(2);
        Mockito.when(holdingsEntity.getOwningInstitutionHoldingsId()).thenReturn("222");
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.anyString(), Mockito.anyInt())).thenReturn(holdingsEntity);
        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        holdingsEntities.add(holdingsEntity);
        Mockito.when(bibliographicEntity.getHoldingsEntities()).thenReturn(holdingsEntities);
        Mockito.when(entityManager.merge(Mockito.any())).thenReturn(bibliographicEntity);
        Mockito.when(bibliographicEntity.getOwningInstitutionId()).thenReturn(1);
        Mockito.when(bibliographicEntity.getOwningInstitutionBibId()).thenReturn("111");
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(), Mockito.anyString())).thenReturn(bibliographicEntity);
        BibliographicEntity savedBibliographicEntity = bibliographicRepositoryDAO.saveOrUpdate(bibliographicEntity);
        assertNotNull(savedBibliographicEntity);
    }
}
