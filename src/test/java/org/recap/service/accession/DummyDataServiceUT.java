package org.recap.service.accession;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.OwningInstitutionIDSequenceRepository;
import org.recap.util.CommonUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 28/4/17.
 */
public class DummyDataServiceUT extends BaseTestCaseUT {

    @InjectMocks
    private DummyDataService dummyDataService;

    @Mock
    OwningInstitutionIDSequenceRepository owningInstitutionIDSequenceRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    AccessionDAO accessionDAO;

    @Mock
    CommonUtil commonUtil;

    @Test
    public void createDummyDataAsIncomplete(){
        Mockito.when(owningInstitutionIDSequenceRepository.saveAndFlush(Mockito.any())).thenReturn(new OwningInstitutionIDSequence());
        List<CollectionGroupEntity> collectionGroupEntityList=new ArrayList<>();
        collectionGroupEntityList.add(getCollectionGroupEntity());
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(collectionGroupEntityList);
        List<ItemStatusEntity> itemStatusEntities=new ArrayList<>();
        ItemStatusEntity itemStatusEntity=new ItemStatusEntity();
        itemStatusEntity.setStatusCode("RecentlyReturned");
        itemStatusEntity.setStatusDescription("RecentlyReturned");
        itemStatusEntities.add(itemStatusEntity);
        Mockito.when(itemStatusDetailsRepository.findAll()).thenReturn(itemStatusEntities);
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        StringBuilder file=new StringBuilder();
        file.append("test");
        Mockito.when(commonUtil.getContentByFileName(Mockito.anyString())).thenReturn(file);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        BibliographicEntity bibliographicEntity = dummyDataService.createDummyDataAsIncomplete(1,"3245678232","PA",imsLocationEntity);
        assertNotNull(bibliographicEntity);
        assertEquals(RecapCommonConstants.INCOMPLETE_STATUS,bibliographicEntity.getCatalogingStatus());
        assertEquals(RecapConstants.DUMMY_CALL_NUMBER_TYPE,bibliographicEntity.getItemEntities().get(0).getCallNumberType());
        assertEquals(RecapCommonConstants.DUMMYCALLNUMBER,bibliographicEntity.getItemEntities().get(0).getCallNumber());
        assertEquals(RecapCommonConstants.INCOMPLETE_STATUS,bibliographicEntity.getItemEntities().get(0).getCatalogingStatus());
    }

    @Test
    public void getCollectionGroupMapException(){
        Mockito.when(owningInstitutionIDSequenceRepository.saveAndFlush(Mockito.any())).thenReturn(new OwningInstitutionIDSequence());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        StringBuilder file=new StringBuilder();
        file.append("test");
        Mockito.when(commonUtil.getContentByFileName(Mockito.anyString())).thenReturn(file);
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        Mockito.when(itemStatusDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        BibliographicEntity bibliographicEntity = dummyDataService.createDummyDataAsIncomplete(1,"3245678232","PA",imsLocationEntity);
        assertNotNull(bibliographicEntity);
        assertEquals(RecapCommonConstants.INCOMPLETE_STATUS,bibliographicEntity.getCatalogingStatus());
        assertEquals(RecapConstants.DUMMY_CALL_NUMBER_TYPE,bibliographicEntity.getItemEntities().get(0).getCallNumberType());
        assertEquals(RecapCommonConstants.DUMMYCALLNUMBER,bibliographicEntity.getItemEntities().get(0).getCallNumber());
        assertEquals(RecapCommonConstants.INCOMPLETE_STATUS,bibliographicEntity.getItemEntities().get(0).getCatalogingStatus());
    }

    @Test
    public void getItemStatusMapException(){
        Mockito.when(owningInstitutionIDSequenceRepository.saveAndFlush(Mockito.any())).thenReturn(new OwningInstitutionIDSequence());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        StringBuilder file=new StringBuilder();
        file.append("test");
        Mockito.when(commonUtil.getContentByFileName(Mockito.anyString())).thenReturn(file);
        Map<String,Integer> collectionGroupMap=new HashMap<>();
        collectionGroupMap.put(RecapCommonConstants.NOT_AVAILABLE,2);
        Map<String,Integer> itemStatusMap=new HashMap<>();
        itemStatusMap.put(RecapCommonConstants.NOT_AVAILABLE_CGD,1);
        ReflectionTestUtils.setField(dummyDataService,"collectionGroupMap",collectionGroupMap);
        ReflectionTestUtils.setField(dummyDataService,"itemStatusMap",itemStatusMap);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        BibliographicEntity bibliographicEntity = dummyDataService.createDummyDataAsIncomplete(1,"3245678232","PA",imsLocationEntity);
        assertNotNull(bibliographicEntity);
        assertEquals(RecapCommonConstants.INCOMPLETE_STATUS,bibliographicEntity.getCatalogingStatus());
        assertEquals(RecapConstants.DUMMY_CALL_NUMBER_TYPE,bibliographicEntity.getItemEntities().get(0).getCallNumberType());
        assertEquals(RecapCommonConstants.DUMMYCALLNUMBER,bibliographicEntity.getItemEntities().get(0).getCallNumber());
        assertEquals(RecapCommonConstants.INCOMPLETE_STATUS,bibliographicEntity.getItemEntities().get(0).getCatalogingStatus());
    }

    @Test
    public void createDummyDataAsIncompleteException(){
        Mockito.when(owningInstitutionIDSequenceRepository.saveAndFlush(Mockito.any())).thenReturn(new OwningInstitutionIDSequence());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        BibliographicEntity bibliographicEntity = dummyDataService.createDummyDataAsIncomplete(1,"3245678232","PA",imsLocationEntity);
        assertNotNull(bibliographicEntity);
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setCallNumberType(RecapConstants.DUMMY_CALL_NUMBER_TYPE);
        itemEntity.setCallNumber(RecapCommonConstants.DUMMYCALLNUMBER);
        itemEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        return bibliographicEntity;
    }

    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity=new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupDescription("Private");
        collectionGroupEntity.setId(3);
        collectionGroupEntity.setCollectionGroupCode("Private");
        return collectionGroupEntity;
    }
}
