package org.recap.controllerit;

import org.junit.jupiter.api.Test;
import org.recap.ScsbCommonConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PurgeControllerIT extends BaseControllerUT {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Test
    public void testPurgeEmailAddress() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        MvcResult mvcResult = this.mockMvc.perform(get("/purge/purgeEmailAddress")
                ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        assertTrue(result.contains(ScsbCommonConstants.SUCCESS));
    }

    @Test
    public void testPurgeExceptionRequests() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        MvcResult mvcResult = this.mockMvc.perform(get("/purge/purgeExceptionRequests")
                ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        assertTrue(result.contains(ScsbCommonConstants.SUCCESS));
    }

    @Test
    public void testPurgeAccessionRequests() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        MvcResult mvcResult = this.mockMvc.perform(get("/purge/purgeAccessionRequests")
                ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        assertTrue(result.contains(ScsbCommonConstants.SUCCESS));
    }

    private RequestItemEntity createRequestItem() {
        InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode("PUL");

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();

        RequestTypeEntity requestTypeEntity = requestTypeDetailsRepository.findByrequestTypeCode("RECALL");
        RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findById(3).orElse(null);

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getId());
        requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(bibliographicEntity.getItemEntities().get(0));
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setPatronId("1");
        requestItemEntity.setStopCode("test");
        requestItemEntity.setRequestingInstitutionId(2);
        requestItemEntity.setRequestStatusId(3);
        requestItemEntity.setNotes("test");
        requestItemEntity.setEmailId("test@gmail.com");
        RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.saveAndFlush(requestItemEntity);
        entityManager.refresh(savedRequestItemEntity);
        return requestItemEntity;
    }

    private BibliographicEntity saveBibSingleHoldingsSingleItem() {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("test");
        holdingsEntity.setLastUpdatedBy("test");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("8956");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("4598");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setImsLocationId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return bibliographicEntity;

    }
}
