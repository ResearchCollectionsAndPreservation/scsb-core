package org.recap.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.util.SecurityUtil;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by akulak on 20/9/17.
 */
@ExtendWith(MockitoExtension.class)
public class EncryptEmailAddressUT {

    @InjectMocks
    EncryptEmailAddressService encryptEmailAddressService;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    SecurityUtil securityUtil;

    public static final String REQUEST_ID = "requestId";


    @Test
    public void checkEmailAddressEncryption() {
        Mockito.when(requestItemDetailsRepository.count()).thenReturn(0L);
        String result = encryptEmailAddressService.encryptEmailAddress();
        assertNotNull(result);
    }

    @Test
    public void encryptEmailAddress() {
        Mockito.when(requestItemDetailsRepository.count()).thenReturn(10L);
        List<RequestItemEntity> requestItemEntityListToSave = new ArrayList<>();
        RequestItemEntity requestItemEntity = createRequestItem();
        requestItemEntityListToSave.add(requestItemEntity);
        Pageable pageable = PageRequest.of(0, 1000, Sort.Direction.ASC, REQUEST_ID);
        Page<RequestItemEntity> page = new PageImpl<>(requestItemEntityListToSave);
        Mockito.when(requestItemDetailsRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(securityUtil.getEncryptedValue(requestItemEntity.getEmailId())).thenReturn("encrypted@gmail.com");
        Mockito.when(requestItemDetailsRepository.saveAll(requestItemEntityListToSave)).thenReturn(requestItemEntityListToSave);
        String encryptEmailAddress = encryptEmailAddressService.encryptEmailAddress();
        assertNotNull(encryptEmailAddress);
    }

    @Test
    public void encryptEmailAddressException() {
        Mockito.when(requestItemDetailsRepository.count()).thenReturn(10L);
        List<RequestItemEntity> requestItemEntityListToSave = new ArrayList<>();
        RequestItemEntity requestItemEntity = createRequestItem();
        requestItemEntityListToSave.add(requestItemEntity);
        Pageable pageable = PageRequest.of(0, 1000, Sort.Direction.ASC, REQUEST_ID);
        Page<RequestItemEntity> page = new PageImpl<>(requestItemEntityListToSave);
        Mockito.when(requestItemDetailsRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(securityUtil.getEncryptedValue(requestItemEntity.getEmailId())).thenReturn("encrypted@gmail.com");
        Mockito.when(requestItemDetailsRepository.saveAll(requestItemEntityListToSave)).thenThrow(new NullPointerException());
        String encryptEmailAddress = encryptEmailAddressService.encryptEmailAddress();
        assertNotNull(encryptEmailAddress);
    }


    private RequestItemEntity createRequestItem() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recall");
        requestTypeEntity.setRequestTypeDesc("Recall");
        //RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        //assertNotNull(savedRequestTypeEntity);

        //RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findById(3).orElse(null);

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getId());
        requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
        // requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setRequestingInstitutionId(2);
        requestItemEntity.setStopCode("test");
        requestItemEntity.setNotes("test");
        requestItemEntity.setItemEntity(bibliographicEntity.getItemEntities().get(0));
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setPatronId("1");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(3);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setEmailId("test@gmail.com");
        requestItemEntity.setLastUpdatedDate(new Date());
        //RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.saveAndFlush(requestItemEntity);
        // entityManager.refresh(savedRequestItemEntity);
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
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        //BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        // entityManager.refresh(savedBibliographicEntity);
        return bibliographicEntity;

    }
}
