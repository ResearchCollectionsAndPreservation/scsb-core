package org.recap.service.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by premkb on 18/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SetupDataServiceUT {

    @InjectMocks
    private SetupDataService setupDataService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    InstitutionDetailsRepository mockInstitutionDetailsRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void getItemStatusIdCodeMap(){
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        Mockito.when(repositoryService.getItemStatusDetailsRepository()).thenReturn(itemStatusDetailsRepository);
        Mockito.when(repositoryService.getItemStatusDetailsRepository().findAll()).thenReturn(Arrays.asList(itemStatusEntity));
        Map<Integer,String> itemStatusIdCodeMap = setupDataService.getItemStatusIdCodeMap();
        assertNotNull(itemStatusIdCodeMap);
        String itemStatusCode = itemStatusIdCodeMap.get(1);
        assertEquals(RecapCommonConstants.AVAILABLE,itemStatusCode);
    }
    @Test
    public void getItemStatusIdCodeMapForException(){
        Map<Integer,String> itemStatusIdCodeMap = setupDataService.getItemStatusIdCodeMap();
        assertNotNull(itemStatusIdCodeMap);
    }
    @Test
    public void getItemStatusCodeIdMap(){
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        Mockito.when(repositoryService.getItemStatusDetailsRepository()).thenReturn(itemStatusDetailsRepository);
        Mockito.when(repositoryService.getItemStatusDetailsRepository().findAll()).thenReturn(Arrays.asList(itemStatusEntity));
        Map<String,Integer> itemStatusCodeIdMap = setupDataService.getItemStatusCodeIdMap();
        assertNotNull(itemStatusCodeIdMap);
        Integer itemStatusId = itemStatusCodeIdMap.get(RecapCommonConstants.AVAILABLE);
        assertEquals("1",itemStatusId.toString());
    }
    @Test
    public void getItemStatusCodeIdMapForException(){
        Map<String,Integer> itemStatusCodeIdMap = setupDataService.getItemStatusCodeIdMap();
        assertNotNull(itemStatusCodeIdMap);
    }
    @Test
    public void getInstitutionIdCodeMap(){
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(repositoryService.getInstitutionDetailsRepository()).thenReturn(mockInstitutionDetailsRepository);
        Mockito.when(repositoryService.getInstitutionDetailsRepository().findAll()).thenReturn(Arrays.asList(institutionEntity));
        Map<Integer,String> institutionEntityMap = setupDataService.getInstitutionIdCodeMap();
        assertNotNull(institutionEntityMap);
        String itemStatusCode = institutionEntityMap.get(1);
        assertEquals(RecapCommonConstants.PRINCETON,itemStatusCode);
    }
    @Test
    public void getInstitutionIdCodeMapForException(){
        Map<Integer,String> institutionEntityMap = setupDataService.getInstitutionIdCodeMap();
        assertNotNull(institutionEntityMap);
    }
    @Test
    public void getInstitutionCodeIdMap(){
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(repositoryService.getInstitutionDetailsRepository()).thenReturn(mockInstitutionDetailsRepository);
        Mockito.when(repositoryService.getInstitutionDetailsRepository().findAll()).thenReturn(Arrays.asList(institutionEntity));
        Map<String,Integer> institutionEntityMapId = setupDataService.getInstitutionCodeIdMap();
        assertNotNull(institutionEntityMapId);
        Integer itemStatusId = institutionEntityMapId.get(RecapCommonConstants.PRINCETON);
        assertEquals("1",itemStatusId.toString());
    }
    @Test
    public void getInstitutionCodeIdMapForException(){
        Map<String,Integer> institutionEntityMapId = setupDataService.getInstitutionCodeIdMap();
        assertNotNull(institutionEntityMapId);
    }
    @Test
    public void getCollectionGroupMap(){
        CollectionGroupEntity collectionGroupEntity = getCollectionGroupEntity();
        Mockito.when(repositoryService.getCollectionGroupDetailsRepository()).thenReturn(collectionGroupDetailsRepository);
        Mockito.when(repositoryService.getCollectionGroupDetailsRepository().findAll()).thenReturn(Arrays.asList(collectionGroupEntity));
        Map<Integer,String> collectionGroupMap = setupDataService.getCollectionGroupMap();
        assertNotNull(collectionGroupMap);
    }
    @Test
    public void getCollectionGroupMapForException(){
        Map<Integer,String> collectionGroupMap = setupDataService.getCollectionGroupMap();
        assertNotNull(collectionGroupMap);
    }

    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("GA");
        collectionGroupEntity.setCollectionGroupDescription("collection");
        collectionGroupEntity.setCreatedDate(new Date());
        collectionGroupEntity.setLastUpdatedDate(new Date());
        return collectionGroupEntity;
    }

    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("SUCCESS");
        return itemStatusEntity;
    }
    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }
}
