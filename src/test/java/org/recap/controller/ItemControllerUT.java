package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by hemalathas on 16/11/16.
 */

public class ItemControllerUT extends BaseTestCaseUT {


    @InjectMocks
    ItemController itemController;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Test
    public void testItemDetails() throws Exception {
        Mockito.when(itemDetailsRepository.findByBarcodeInAndComplete(Mockito.anyList())).thenReturn(saveBibSingleHoldingsMultipleItem().getItemEntities());
        List<ItemEntity> itemEntityList = itemController.findByBarcodeIn("00009,00010");
        assertNotNull(itemEntityList);
        assertEquals(2,itemEntityList.size());
    }

    public BibliographicEntity saveBibSingleHoldingsMultipleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));
        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);
        ItemEntity itemEntity1 = getItemEntity("00009");
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity1.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity2 = getItemEntity("00010");
        itemEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity2.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        return bibliographicEntity;
    }

    private ItemEntity getItemEntity(String barcode) {
        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setOwningInstitutionItemId("56735");
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setBarcode(barcode);
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setCatalogingStatus("Complete");
        return itemEntity1;
    }

    private HoldingsEntity getHoldingsEntity(Random random, Integer institutionId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(institutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        return holdingsEntity;
    }

    private BibliographicEntity getBibliographicEntity(Integer institutionId, String owningInstitutionBibId1) {
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(institutionId);
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId1);
        return bibliographicEntity1;
    }
}
