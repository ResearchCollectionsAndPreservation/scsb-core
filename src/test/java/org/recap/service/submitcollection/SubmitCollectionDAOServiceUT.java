package org.recap.service.submitcollection;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.jpa.*;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.model.submitcollection.BoundWithBibliographicEntityObject;
import org.recap.model.submitcollection.NonBoundWithBibliographicEntityObject;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.service.common.RepositoryService;
import org.recap.service.common.SetupDataService;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionDAOServiceUT {

    @InjectMocks
    SubmitCollectionDAOService submitCollectionDAOService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private EntityManager entityManager;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    BibliographicDetailsRepository  bibliographicDetailsRepository;

    @Mock
    private SetupDataService setupDataService;

    @Mock
    private SubmitCollectionReportHelperService submitCollectionReportHelperService;

    @Mock
    private SubmitCollectionValidationService submitCollectionValidationService;

    @Mock
    private SubmitCollectionHelperService submitCollectionHelperService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(submitCollectionDAOService, "nonHoldingIdInstitution", "NYPL");
    }


    @Test
    public void updateBibliographicEntityInBatchForNonBoundWith() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject();
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(1,"NYPL");
        institutionEntityMap.put(2,"Available");

        Map institutionEntityMap1 = new HashMap();
        institutionEntityMap1.put(1,"Available");
        institutionEntityMap1.put(2,"NYPL");
        Mockito.when(setupDataService.getItemStatusIdCodeMap().get(1)).thenReturn(institutionEntityMap1);
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(incomingBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(false);
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }
    @Test
    public void updateBibliographicEntityInBatchForNonBoundWithExistingBound() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject();
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(true);
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }
    @Test
    public void updateBibliographicEntityInBatchForNonBoundWithDifferentOwingInsBibId() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("234566");
        nonBoundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(false);
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }
    @Test
    public void updateBibliographicEntityInBatchForBoundWith(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity());
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity().getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity());
        Map<String,BibliographicEntity> fetchedOwnInstBibIdBibliographicEntityMap = new HashMap<>();
        fetchedOwnInstBibIdBibliographicEntityMap.put("34558",getBibliographicEntity());
        Mockito.when(submitCollectionValidationService.getOwnInstBibIdBibliographicEntityMap(any())).thenReturn(fetchedOwnInstBibIdBibliographicEntityMap);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountIsSameAsExistingItem(any(),any(),any())).thenReturn(true);
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        List<BibliographicEntity> bibliographicEntities = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }
    @Test
    public void updateBibliographicEntityInBatchForBoundWithGreaterIncomingBibliographicEntity(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();

        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("245466");
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity();
        bibliographicEntity1.setOwningInstitutionBibId("657786");
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        bibliographicEntities.add(bibliographicEntity1);
        boundWithBibliographicEntityObject.setBibliographicEntityList(bibliographicEntities);
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity());
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity().getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity());
        Map institutionEntityMap1 = new HashMap();
        institutionEntityMap1.put(1,"Available");
        institutionEntityMap1.put(2,"NYPL");
        Mockito.when(setupDataService.getItemStatusIdCodeMap().get(1)).thenReturn(institutionEntityMap1);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountGreaterThanExistingItem(any(),any(),any())).thenReturn(true);
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(repositoryService.getBibliographicDetailsRepository().saveAndFlush(any())).thenReturn(getBibliographicEntity());
        List<BibliographicEntity> bibliographicEntities1 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities1);
        Mockito.when(submitCollectionHelperService.getBibliographicEntityIfExist(any(),any())).thenReturn(getBibliographicEntity());
        Map<String,BibliographicEntity> fetchedOwnInstBibIdBibliographicEntityMap = new HashMap<>();
        fetchedOwnInstBibIdBibliographicEntityMap.put("245466",getBibliographicEntity());
        Mockito.when(submitCollectionValidationService.getOwnInstBibIdBibliographicEntityMap(any())).thenReturn(fetchedOwnInstBibIdBibliographicEntityMap);
        Mockito.when(repositoryService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(any(),any())).thenReturn(getBibliographicEntity());
        List<BibliographicEntity> bibliographicEntities2 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities2);
    }
    @Test
    public void updateBibliographicEntityInBatchForBoundWithGreaterExistingBibliographicEntity(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();

        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity());
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity().getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity());
//        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountIsSameAsExistingItem(submitCollectionReportInfoMap,fetchedBarcodeItemEntityMap,boundWithBibliographicEntityObject.getBibliographicEntityList())).thenReturn(true);
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        List<BibliographicEntity> bibliographicEntities1 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities1);
    }
    @Test
    public void prepareExceptionReport(){
        List<String> incomingItemBarcodeList = new ArrayList<>();
        incomingItemBarcodeList.add("123456");
        List<String> fetchedItemBarcodeList = new ArrayList<>();
        fetchedItemBarcodeList.add("67380");
        String message = "test";
        List<ItemEntity> itemEntityList = getBibliographicEntity().getItemEntities();
        Map<String,ItemEntity> incomingBarcodeItemEntityMapFromBibliographicEntityList = new HashMap<>();
        incomingBarcodeItemEntityMapFromBibliographicEntityList.put("123456",getBibliographicEntity().getItemEntities().get(0));
        Map<String,List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Mockito.when(submitCollectionReportHelperService.isBarcodeAlreadyAdded("123456",submitCollectionReportInfoMap)).thenReturn(false);
        Mockito.when(submitCollectionReportHelperService.isBarcodeAlreadyAdded(itemEntityList.get(0).getBarcode(),submitCollectionReportInfoMap)).thenReturn(false);
//        Mockito.doNothing().when(submitCollectionReportHelperService).setSubmitCollectionExceptionReportInfo(itemEntityList,submitCollectionReportInfoMap.get(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_LIST), message);
        submitCollectionDAOService.prepareExceptionReport(incomingItemBarcodeList,fetchedItemBarcodeList,incomingBarcodeItemEntityMapFromBibliographicEntityList,submitCollectionReportInfoMap);
    }
    @Test
    public  void updateDummyRecord(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List< ItemChangeLogEntity > itemChangeLogEntityList = new ArrayList<>();
        ItemChangeLogEntity itemChangeLogEntity = getItemChangeLogEntity();
        itemChangeLogEntityList.add(itemChangeLogEntity);
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity();
        List<ItemEntity> itemEntities = getBibliographicEntity().getItemEntities();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(bibliographicEntity.getItemEntities())).thenReturn(itemEntities);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(repositoryService.getItemChangeLogDetailsRepository()).thenReturn(itemChangeLogDetailsRepository);
        Mockito.doNothing().when(bibliographicDetailsRepository).delete(fetchBibliographicEntity);
        Mockito.doNothing().when(bibliographicDetailsRepository).flush();
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(bibliographicEntity)).thenReturn(bibliographicEntity);
        Mockito.when(repositoryService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId())).thenReturn(bibliographicEntity);
//        Mockito.when(repositoryService.getItemChangeLogDetailsRepository().saveAll(itemChangeLogEntityList)).thenReturn(itemChangeLogEntityList);
        Mockito.doNothing().when(entityManager).refresh(bibliographicEntity);
        BibliographicEntity bibliographicEntity1 = submitCollectionDAOService.updateDummyRecord(bibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity);
        assertNotNull(bibliographicEntity1);
    }

    @Test
    public  void updateDummyRecordWithoutCollectionGroupId(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity2();
        bibliographicEntity.getItemEntities().get(0).setCollectionGroupId(null);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity();
        List<ItemEntity> itemEntities = getBibliographicEntity().getItemEntities();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(bibliographicEntity.getItemEntities())).thenReturn(itemEntities);
        BibliographicEntity bibliographicEntity1 = submitCollectionDAOService.updateDummyRecord(bibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity);
        assertNotNull(bibliographicEntity1);
    }
    @Test
    public void updateDummyRecordForNonBoundWith(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity();
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.doNothing().when(bibliographicDetailsRepository).delete(fetchBibliographicEntity);
        Mockito.doNothing().when(bibliographicDetailsRepository).flush();
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(incomingBibliographicEntity)).thenReturn(incomingBibliographicEntity);
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(incomingBibliographicEntity.getItemEntities())).thenReturn(Collections.EMPTY_LIST);
        submitCollectionDAOService.updateDummyRecordForNonBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList);
    }
    @Test
    public void updateDummyRecordForNonBoundWithoutCollectionGroupId(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        incomingBibliographicEntity.getItemEntities().get(0).setCollectionGroupId(null);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity();
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(incomingBibliographicEntity.getItemEntities())).thenReturn(Collections.EMPTY_LIST);
        submitCollectionDAOService.updateDummyRecordForNonBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList);
    }
    @Test
    public void updateDummyRecordForNonBoundWithfetchedItemBasedOnOwningInstitutionItemId(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity();
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        List<ItemEntity> fetchedItemBasedOnOwningInstitutionItemId = getBibliographicEntity().getItemEntities();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(incomingBibliographicEntity.getItemEntities())).thenReturn(fetchedItemBasedOnOwningInstitutionItemId);
        submitCollectionDAOService.updateDummyRecordForNonBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList);
    }
    @Test
    public void updateDummyRecordForBoundWith(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity2();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity();
        List< ItemChangeLogEntity > itemChangeLogEntityList = new ArrayList<>();
        boolean deleteDummyRecord = true;
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(123456);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.doNothing().when(bibliographicDetailsRepository).delete(fetchBibliographicEntity);
        Mockito.doNothing().when(bibliographicDetailsRepository).flush();
        Mockito.when(repositoryService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(incomingBibliographicEntity.getOwningInstitutionId(), incomingBibliographicEntity.getOwningInstitutionBibId())).thenReturn(getBibliographicEntity());
        BibliographicEntity bibliographicEntity = submitCollectionDAOService.updateDummyRecordForBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList,deleteDummyRecord,processedBibIds);
        assertNotNull(bibliographicEntity);
    }
    @Test
    public void updateDummyRecordForBoundWithoutCollectionGroupId(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        incomingBibliographicEntity.getItemEntities().get(0).setCollectionGroupId(null);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity();
        List< ItemChangeLogEntity > itemChangeLogEntityList = new ArrayList<>();
        boolean deleteDummyRecord = true;
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(123456);
        BibliographicEntity bibliographicEntity = submitCollectionDAOService.updateDummyRecordForBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList,deleteDummyRecord,processedBibIds);
        assertNotNull(bibliographicEntity);
    }
    private BoundWithBibliographicEntityObject getBoundWithBibliographicEntityObject(){
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = new BoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity2();
        boundWithBibliographicEntityObject.setBarcode("123456");
        boundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        return boundWithBibliographicEntityObject;
    }
    private NonBoundWithBibliographicEntityObject getNonBoundWithBibliographicEntityObject(){
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = new NonBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        nonBoundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        nonBoundWithBibliographicEntityObject.setOwningInstitutionBibId("1577261074");
        return nonBoundWithBibliographicEntityObject;
    }
    private Map<String, List< SubmitCollectionReportInfo >> getSubmitCollectionReportInfoMap(){
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setMessage("SUCCESS");
        submitCollectionReportInfo.setCustomerCode("PA");
        submitCollectionReportInfo.setItemBarcode("123456");
        submitCollectionReportInfo.setOwningInstitution("PUL");
        submitCollectionReportInfoMap.put("1",Arrays.asList(submitCollectionReportInfo));
        return submitCollectionReportInfoMap;
    }
    private ItemChangeLogEntity getItemChangeLogEntity() {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setNotes("test");
        itemChangeLogEntity.setOperationType("RECALL");
        itemChangeLogEntity.setRecordId(1);
        itemChangeLogEntity.setUpdatedBy("TEST");
        itemChangeLogEntity.setUpdatedDate(new Date());
        return itemChangeLogEntity;
    }

    String content = "<?xml version=\"1.0\" ?>\n" +
            "<bibRecords>\n" +
            "    <bibRecord>\n" +
            "        <bib>\n" +
            "            <owningInstitutionId>NYPL</owningInstitutionId>\n" +
            "            <owningInstitutionBibId>.b153286131</owningInstitutionBibId>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                    <record>\n" +
            "                        <controlfield tag=\"001\">47764496</controlfield>\n" +
            "                        <controlfield tag=\"003\">OCoLC</controlfield>\n" +
            "                        <controlfield tag=\"005\">20021018083242.7</controlfield>\n" +
            "                        <controlfield tag=\"008\">010604s2000 it a bde 000 0cita</controlfield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "                            <subfield code=\"a\">2001386785</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "                            <subfield code=\"a\">8880898620</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "                            <subfield code=\"a\">DLC</subfield>\n" +
            "                            <subfield code=\"c\">DLC</subfield>\n" +
            "                            <subfield code=\"d\">NYP</subfield>\n" +
            "                            <subfield code=\"d\">OCoLC</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"042\">\n" +
            "                            <subfield code=\"a\">pcc</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"043\">\n" +
            "                            <subfield code=\"a\">e-it---</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"049\">\n" +
            "                            <subfield code=\"a\">NYPG</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
            "                            <subfield code=\"a\">GV942.7.A1</subfield>\n" +
            "                            <subfield code=\"b\">D59 2000</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"0\" ind2=\"0\" tag=\"245\">\n" +
            "                            <subfield code=\"a\">Dizionario biografico enciclopedico di un secolo del calcio italiano /\n" +
            "                            </subfield>\n" +
            "                            <subfield code=\"c\">a cura di Marco Sappino.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"1\" ind2=\"4\" tag=\"246\">\n" +
            "                            <subfield code=\"a\">Dizionario del calcio italiano</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "                            <subfield code=\"a\">Milano :</subfield>\n" +
            "                            <subfield code=\"b\">Baldini &amp; Castoldi,</subfield>\n" +
            "                            <subfield code=\"c\">c2000.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "                            <subfield code=\"a\">2 v., (2147 p.) :</subfield>\n" +
            "                            <subfield code=\"b\">ill. ;</subfield>\n" +
            "                            <subfield code=\"c\">22 cm.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"3\" tag=\"440\">\n" +
            "                            <subfield code=\"a\">Le boe ;</subfield>\n" +
            "                            <subfield code=\"v\">43</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "                            <subfield code=\"a\">Includes bibliographical references (p. [2004]-2038).</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"1\" ind2=\" \" tag=\"505\">\n" +
            "                            <subfield code=\"a\">1. Protagonisti -- 2. club e trofei.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                            <subfield code=\"a\">Soccer players</subfield>\n" +
            "                            <subfield code=\"z\">Italy</subfield>\n" +
            "                            <subfield code=\"v\">Biography</subfield>\n" +
            "                            <subfield code=\"v\">Dictionaries.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                            <subfield code=\"a\">Soccer</subfield>\n" +
            "                            <subfield code=\"z\">Italy</subfield>\n" +
            "                            <subfield code=\"v\">Biography</subfield>\n" +
            "                            <subfield code=\"v\">Dictionaries.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                            <subfield code=\"a\">Soccer</subfield>\n" +
            "                            <subfield code=\"z\">Italy</subfield>\n" +
            "                            <subfield code=\"v\">Encyclopedias.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n" +
            "                            <subfield code=\"a\">Sappino, Marco.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"907\">\n" +
            "                            <subfield code=\"a\">.b153286131</subfield>\n" +
            "                            <subfield code=\"c\">m</subfield>\n" +
            "                            <subfield code=\"d\">a</subfield>\n" +
            "                            <subfield code=\"e\">-</subfield>\n" +
            "                            <subfield code=\"f\">ita</subfield>\n" +
            "                            <subfield code=\"g\">it</subfield>\n" +
            "                            <subfield code=\"h\">0</subfield>\n" +
            "                            <subfield code=\"i\">2</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"952\">\n" +
            "                            <subfield code=\"h\">JFD 02-22709</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "                            <subfield code=\"a\">(OCoLC)47764496</subfield>\n" +
            "                        </datafield>\n" +
            "                        <leader>01184nam a22003494a 4500</leader>\n" +
            "                    </record>\n" +
            "                </collection>\n" +
            "            </content>\n" +
            "        </bib>\n" +
            "        <holdings>\n" +
            "            <holding>\n" +
            "                <owningInstitutionHoldingsId/>\n" +
            "                <content>\n" +
            "                    <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                        <record>\n" +
            "                            <datafield ind1=\" \" ind2=\"8\" tag=\"852\">\n" +
            "                                <subfield code=\"b\">rc2ma</subfield>\n" +
            "                                <subfield code=\"h\">JFD 02-22709</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\" \" ind2=\" \" tag=\"866\">\n" +
            "                                <subfield code=\"a\">v. 1</subfield>\n" +
            "                            </datafield>\n" +
            "                        </record>\n" +
            "                    </collection>\n" +
            "                </content>\n" +
            "                <items>\n" +
            "                    <content>\n" +
            "                        <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                            <record>\n" +
            "                                <datafield ind1=\" \" ind2=\" \" tag=\"876\">\n" +
            "                                    <subfield code=\"p\">33433031684909</subfield>\n" +
            "                                    <subfield code=\"h\">In Library Use</subfield>\n" +
            "                                    <subfield code=\"a\">.i116690355</subfield>\n" +
            "                                    <subfield code=\"j\">Available</subfield>\n" +
            "                                    <subfield code=\"t\">1</subfield>\n" +
            "                                    <subfield code=\"3\">v. 1</subfield>\n" +
            "                                </datafield>\n" +
            "                                <datafield ind1=\" \" ind2=\" \" tag=\"900\">\n" +
            "                                    <subfield code=\"a\">Shared</subfield>\n" +
            "                                    <subfield code=\"b\">NA</subfield>\n" +
            "                                </datafield>\n" +
            "                            </record>\n" +
            "                        </collection>\n" +
            "                    </content>\n" +
            "                </items>\n" +
            "            </holding>\n" +
            "        </holdings>\n" +
            "    </bibRecord>\n" +
            "</bibRecords>\n";
    private BibliographicEntity getBibliographicEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(123456);
        bibliographicEntity.setContent(content.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1577261074");
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCatalogingStatus("inComplete");

        List<ItemEntity> itemEntities = new ArrayList<>();
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("843617540");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);
        itemEntities.add(itemEntity);

        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("2435");
        holdingsEntity.setDeleted(false);
        //holdingsEntity.setItemEntities(itemEntities);
        holdingsEntities.add(holdingsEntity);


        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setInstitutionEntity(institutionEntity);
        holdingsEntity.setItemEntities(itemEntities);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        bibliographicEntity.setItemEntities(itemEntities);

        return bibliographicEntity;
    }
    private BibliographicEntity getBibliographicEntity2(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(123456);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1577261074");
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCatalogingStatus("inComplete");

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("843617540");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setInstitutionEntity(institutionEntity);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("34558");
        BibliographicEntity bibliographicEntity2= getBibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("34558");
        bibliographicEntity1.setOwningInstitutionBibId("45568");
        bibliographicEntities.add(bibliographicEntity1);
        bibliographicEntities.add(bibliographicEntity2);
        itemEntity.setBibliographicEntities(bibliographicEntities);
        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

}
