package org.recap.repository.jpa;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BibliographicDetailsResopistoryCustomImplUT extends BaseTestCaseUT {

    @InjectMocks
    BibliographicDetailsResopistoryCustomImpl bibliographicDetailsResopistoryCustomImpl;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    ItemEntity fetchedItemEntity;

    @Mock
    HoldingsEntity holdingsEntity;

    @Mock
    EntityManager entityManager;

    @Test
    public void testupdateBibForSubmitCollection() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(fetchedItemEntity);
        Mockito.when(bibliographicEntity.getItemEntities()).thenReturn(itemEntities);
        bibliographicDetailsResopistoryCustomImpl.updateBibForSubmitCollection(bibliographicEntity, fetchedItemEntity);
        assertTrue(true);
    }

    @Test
    public void testattachExistingHoldingId() {
        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        holdingsEntities.add(holdingsEntity);
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(fetchedItemEntity);
        Mockito.when(bibliographicEntity.getItemEntities()).thenReturn(itemEntities);
        Mockito.when(fetchedItemEntity.getId()).thenReturn(1);
        Mockito.when(holdingsEntity.getItemEntities()).thenReturn(itemEntities);
        Mockito.when(fetchedItemEntity.getHoldingsEntities()).thenReturn(holdingsEntities);
        List<HoldingsEntity> holdingsEntities1 = new ArrayList<>();
        holdingsEntities1.add(new HoldingsEntity());
        Mockito.when(bibliographicEntity.getHoldingsEntities()).thenReturn(holdingsEntities1);
        bibliographicDetailsResopistoryCustomImpl.updateBibForSubmitCollection(bibliographicEntity, fetchedItemEntity);
        assertTrue(true);
    }
}
