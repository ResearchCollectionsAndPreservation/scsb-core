package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by sudhishk on 20/1/17.
 */
public class RequestItemDetailsRepositoryUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(RequestItemDetailsRepositoryUT.class);
    @Autowired
    RequestTypeDetailsRepository requestTypeDetailsRepository;
    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;
    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;
    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testRecallPatronValidation() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByItemBarcode(pageable, "PULTST54333");
        if (requestItemEntities.iterator().hasNext()) {
            logger.info(requestItemEntities.iterator().next().getRequestTypeEntity().getRequestTypeDesc());
            logger.info(requestItemEntities.iterator().next().getRequestStatusEntity().getRequestStatusCode());
        } else {
            logger.info("No Value");
        }
    }

    public RequestItemEntity getRequestItemEntity() throws Exception {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UOC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);
        assertNotNull(entity);

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");
        RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        assertNotNull(savedRequestTypeEntity);

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("Refile");
        requestStatusEntity.setRequestStatusDescription("Refile");
        RequestStatusEntity savedRequestStatusEntity = requestItemStatusDetailsRepository.save(requestStatusEntity);
        assertNotNull(savedRequestStatusEntity);

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getItemId());
        requestItemEntity.setRequestTypeId(savedRequestStatusEntity.getId());
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setRequestStatusId(1);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setPatronId("45678912");
        requestItemEntity.setStopCode("test");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestTypeEntity(savedRequestTypeEntity);
        requestItemEntity.setRequestStatusEntity(savedRequestStatusEntity);
        RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        assertNotNull(savedRequestItemEntity);
        return savedRequestItemEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
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
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("PULTST54333");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        return savedBibliographicEntity;

    }

    @Test
    public void testRequestItem() throws Exception {
        RequestItemEntity requestItemEntity = requestItemDetailsRepository.findById(202).orElse(null);
        assertTrue(true);
    }
}
