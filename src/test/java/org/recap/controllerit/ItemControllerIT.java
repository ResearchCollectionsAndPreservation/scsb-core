package org.recap.controllerit;

import org.junit.jupiter.api.Test;
import org.recap.ScsbCommonConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerIT extends BaseControllerUT {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void findByBarcodeInAndComplete() throws Exception {
        try {
            BibliographicEntity bibliographicEntity = getBibliographicEntity();
            MvcResult mvcResult = this.mockMvc.perform(get("/item/findByBarcodeIn")
                            .param("barcodes", "32101086866140"))
                    .andExpect(status().isOk())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            assertNotNull(result);
            assertTrue(result.contains("32101086866140"));
            int status = mvcResult.getResponse().getStatus();
            assertTrue(status == 200);
        } catch (ObjectOptimisticLockingFailureException e) {
            e.printStackTrace();
        }

    }

    public BibliographicEntity getBibliographicEntity() throws Exception {
        Date today = new Date();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1421");
        bibliographicEntity.setId(1);
        bibliographicEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(today);
        holdingsEntity.setLastUpdatedDate(today);
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionHoldingsId("1621");
        holdingsEntity.setId(1);
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));


        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("843617540");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("32101086866140");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PA");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setId(1);
        itemEntity.setImsLocationId(1);
        itemEntity.setItemLibrary("tst");
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));

        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);

        return savedBibliographicEntity;
    }
}
