package org.recap.service.submitcollection;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.jpa.ItemEntity;
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class SubmitCollectionDAOServiceUT extends BaseTestCaseUT {

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
    public void updateExistingRecordException() throws Exception{
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntityBoundwith("64343","435").getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(itemDetailsRepository.findByBarcodeInAndOwningInstitutionId(Mockito.anyList(),Mockito.anyInt())).thenReturn(itemEntity);
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenThrow(NullPointerException.class);
        BibliographicEntity bibliographicEntity1=getBibliographicEntity("64343");
        bibliographicEntity1.getItemEntities().get(0).setDeleted(true);
        BibliographicEntity bibliographicEntity=submitCollectionDAOService.updateBibliographicEntity(bibliographicEntity1,getSubmitCollectionReportInfoMap(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNull(bibliographicEntity);
    }


    @Test
    public void updateBibliographicEntityBoundwith() throws Exception{
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntityBoundwith("64343","2435").getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(itemDetailsRepository.findByBarcodeInAndOwningInstitutionId(Mockito.anyList(),Mockito.anyInt())).thenReturn(itemEntity);
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        ReflectionTestUtils.setField(repositoryService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        Mockito.when(repositoryService.getItemChangeLogDetailsRepository()).thenCallRealMethod();
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(Mockito.any())).thenReturn(getBibliographicEntity("64343"));
        BibliographicEntity bibliographicEntity=submitCollectionDAOService.updateBibliographicEntity(getBibliographicEntity("64343"),getSubmitCollectionReportInfoMap(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntity);
    }

    @Test
    public void updateBibliographicEntity() throws Exception{
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(itemDetailsRepository.findByBarcodeInAndOwningInstitutionId(Mockito.anyList(),Mockito.anyInt())).thenReturn(itemEntity);
        BibliographicEntity bibliographicEntity=submitCollectionDAOService.updateBibliographicEntity(getBibliographicEntity("64343"),getSubmitCollectionReportInfoMap(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNull(bibliographicEntity);
    }

    @Test
    public void updateBibliographicEntityException() throws Exception{
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(itemDetailsRepository.findByBarcodeInAndOwningInstitutionId(Mockito.anyList(),Mockito.anyInt())).thenReturn(null);
        BibliographicEntity bibliographicEntity=submitCollectionDAOService.updateBibliographicEntity(getBibliographicEntity("64343"),getSubmitCollectionReportInfoMap(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntity);
    }

    @Test
    public void updateBibliographicEntityDummy() throws Exception{
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("d1").getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        ReflectionTestUtils.setField(repositoryService,"bibliographicDetailsRepository",bibliographicDetailsRepository);
        ReflectionTestUtils.setField(repositoryService,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenCallRealMethod();
        Mockito.when(repositoryService.getItemChangeLogDetailsRepository()).thenCallRealMethod();
        Mockito.when(itemDetailsRepository.findByBarcodeInAndOwningInstitutionId(Mockito.anyList(),Mockito.anyInt())).thenReturn(itemEntity);
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(Mockito.any())).thenReturn(getBibliographicEntity("64343"));
        BibliographicEntity bibliographicEntity=submitCollectionDAOService.updateBibliographicEntity(getBibliographicEntity("64343"),getSubmitCollectionReportInfoMap(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntity);
    }


    @Test
    public void updateBibliographicEntityInBatchForNonBoundWith() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject("1577261074");
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity("1577261074");
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        Mockito.when(setupDataService.getInstitutionIdCodeMap()).thenReturn(getInstitutionIdCodeMapValue());
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(false);
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }

    private Map getInstitutionIdCodeMapValue() {
        Map institutionCodeIdMap=new HashMap();
        institutionCodeIdMap.put("NYPL",1);
        return institutionCodeIdMap;
    }

    private Map getItemStatusIdCodeMapValue() {
        Map itemStatusIdCodeMap = new HashMap();
        itemStatusIdCodeMap.put(1, RecapConstants.ITEM_STATUS_AVAILABLE);
        return itemStatusIdCodeMap;
    }


    @Test
    public void updateBibliographicEntityInBatchForNonBoundWithDummy() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject("8d");
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity("1d");
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("d1").getItemEntities();
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        Mockito.when(setupDataService.getInstitutionIdCodeMap()).thenReturn(getInstitutionIdCodeMapValue());
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(repositoryService.getItemChangeLogDetailsRepository()).thenReturn(itemChangeLogDetailsRepository);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(false);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.doNothing().when(bibliographicDetailsRepository).delete(Mockito.any());
        Mockito.doNothing().when(bibliographicDetailsRepository).flush();
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(Mockito.any())).thenReturn(incomingBibliographicEntity);
        Mockito.when(repositoryService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity("1577261074"));
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }

    @Test
    public void updateBibliographicEntityInBatchForNonBoundWithExistingBound() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject("1577261074");
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(true);
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }
    @Test
    public void updateBibliographicEntityInBatchForNonBoundWithDifferentOwingInsBibId() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject("1577261074");
        BibliographicEntity bibliographicEntity = getBibliographicEntity("1577261074");
        bibliographicEntity.setOwningInstitutionBibId("234566");
        nonBoundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(false);
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }

    @Test
    public void updateBibliographicEntityInBatchForBoundWithMismatch(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity("1577261074"));
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity("1577261074"));
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountIsSameAsExistingItem(Mockito.anyMap(),Mockito.anyMap(),Mockito.anyList())).thenReturn(true);
        Map<String,BibliographicEntity> fetchedOwnInstBibIdBibliographicEntityMap=new HashMap<>();
        fetchedOwnInstBibIdBibliographicEntityMap.put("34558",getBibliographicEntity("1577261074"));
        Mockito.when(submitCollectionValidationService.getOwnInstBibIdBibliographicEntityMap(Mockito.anyList())).thenReturn(fetchedOwnInstBibIdBibliographicEntityMap);
        Mockito.doCallRealMethod().when(submitCollectionValidationService).verifyAndSetMisMatchBoundWithOwnInstBibIdIfAny(Mockito.anyList(),Mockito.anyList(),Mockito.anyList(),Mockito.anyList());
        List<BibliographicEntity> bibliographicEntities = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);

    }

    @Test
    public void updateBibliographicEntityInBatchForBoundWith(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity("1577261074"));
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity("1577261074"));
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountIsSameAsExistingItem(Mockito.anyMap(),Mockito.anyMap(),Mockito.anyList())).thenReturn(true);
        Map<String,BibliographicEntity> fetchedOwnInstBibIdBibliographicEntityMap=new HashMap<>();
        fetchedOwnInstBibIdBibliographicEntityMap.put("34558",getBibliographicEntity2("1577261074"));
        Mockito.when(submitCollectionValidationService.getOwnInstBibIdBibliographicEntityMap(Mockito.anyList())).thenReturn(fetchedOwnInstBibIdBibliographicEntityMap);
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        List<BibliographicEntity> bibliographicEntities = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }

    @Test
    public void updateBibliographicEntityInBatchForBoundWithGreaterIncomingBibDummy(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity("1577261074");
        bibliographicEntity.setOwningInstitutionBibId("245466");
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity("1577261074");
        bibliographicEntity1.setOwningInstitutionBibId("657786");
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        bibliographicEntities.add(bibliographicEntity1);
        boundWithBibliographicEntityObject.setBibliographicEntityList(bibliographicEntities);
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("d1").getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity("1577261074"));
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity("1577261074"));
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountGreaterThanExistingItem(Mockito.anyMap(),Mockito.anyList(),Mockito.anyList())).thenReturn(true);
        Mockito.when(submitCollectionHelperService.getBibliographicEntityIfExist(Mockito.anyString(),Mockito.anyInt())).thenReturn(getBibliographicEntity("1577261074"));
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity("1577261074"));
        List<BibliographicEntity> bibliographicEntities1 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities1);
    }

    @Test
    public void updateBibliographicEntityInBatchForBoundWithGreaterIncomingBib(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity("1577261074");
        bibliographicEntity.setOwningInstitutionBibId("245466");
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity("1577261074");
        bibliographicEntity1.setOwningInstitutionBibId("657786");
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        bibliographicEntities.add(bibliographicEntity1);
        boundWithBibliographicEntityObject.setBibliographicEntityList(bibliographicEntities);
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity("1577261074"));
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity("1577261074"));
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountGreaterThanExistingItem(Mockito.anyMap(),Mockito.anyList(),Mockito.anyList())).thenReturn(true);
        Mockito.when(submitCollectionHelperService.getBibliographicEntityIfExist(Mockito.anyString(),Mockito.anyInt())).thenReturn(getBibliographicEntity("1577261074"));
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity("1577261074"));
        Map<String,BibliographicEntity> fetchedOwnInstBibIdBibliographicEntityMap=new HashMap<>();
        fetchedOwnInstBibIdBibliographicEntityMap.put("245466",getBibliographicEntity("1577261074"));
        Mockito.when(submitCollectionValidationService.getOwnInstBibIdBibliographicEntityMap(Mockito.anyList())).thenReturn(fetchedOwnInstBibIdBibliographicEntityMap);
        List<BibliographicEntity> bibliographicEntities1 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities1);
    }

    @Test
    public void updateBibliographicEntityInBatchForBoundWithGreaterIncomingBibComplete(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity("1577261074");
        bibliographicEntity.setOwningInstitutionBibId("245466");
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity("1577261074");
        bibliographicEntity1.setOwningInstitutionBibId("657786");
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        bibliographicEntities.add(bibliographicEntity1);
        boundWithBibliographicEntityObject.setBibliographicEntityList(bibliographicEntities);
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<ItemEntity> itemEntity = getBibliographicEntity("1577261074").getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity("1577261074"));
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity("1577261074"));
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(Arrays.asList("123456"),1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountGreaterThanExistingItem(Mockito.anyMap(),Mockito.anyList(),Mockito.anyList())).thenReturn(true);
        Mockito.when(submitCollectionHelperService.getBibliographicEntityIfExist(Mockito.anyString(),Mockito.anyInt())).thenReturn(null);
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(Mockito.any())).thenReturn(getBibliographicEntity("1577261074"));

        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity("1577261074"));
        List<BibliographicEntity> bibliographicEntities1 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities1);
    }

    @Test
    public void updateBibliographicEntityInBatchForBoundWithGreaterExistingBibliographicEntityelse(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntityBoundwith("1577261074","2435").getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity("1577261074"));
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity("1577261074"));
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountLesserThanExistingItem(Mockito.anyMap(),Mockito.anyList(),Mockito.anyList(),Mockito.anyList(),Mockito.any())).thenReturn(true);
        List<BibliographicEntity> bibliographicEntities1 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities1);
    }

    @Test
    public void updateBibliographicEntityInBatchForBoundWithGreaterExistingBibliographicEntityif(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntityBoundwith("1577261074","2435").getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity("1577261074"));
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity("1577261074"));
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,1)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountLesserThanExistingItem(Mockito.anyMap(),Mockito.anyList(),Mockito.anyList(),Mockito.anyList(),Mockito.any())).thenReturn(true);
        Map<String,BibliographicEntity> fetchedOwnInstBibIdBibliographicEntityMap=new HashMap<>();
        fetchedOwnInstBibIdBibliographicEntityMap.put("64343",getBibliographicEntity("1577261074"));
        Mockito.when(submitCollectionValidationService.getOwnInstBibIdBibliographicEntityMap(Mockito.anyList())).thenReturn(fetchedOwnInstBibIdBibliographicEntityMap);
        Mockito.when(setupDataService.getItemStatusIdCodeMap()).thenReturn(getItemStatusIdCodeMapValue());
        List<BibliographicEntity> bibliographicEntities1 = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,1,submitCollectionReportInfoMap,getIntegers(),idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities1);
    }

    @Test
    public void prepareExceptionReport(){
        List<String> incomingItemBarcodeList = new ArrayList<>();
        incomingItemBarcodeList.add("123456");
        List<String> fetchedItemBarcodeList = new ArrayList<>();
        fetchedItemBarcodeList.add("67380");
        List<ItemEntity> itemEntityList = getBibliographicEntity("1577261074").getItemEntities();
        Map<String,ItemEntity> incomingBarcodeItemEntityMapFromBibliographicEntityList = new HashMap<>();
        incomingBarcodeItemEntityMapFromBibliographicEntityList.put("123456",getBibliographicEntity("1577261074").getItemEntities().get(0));
        Map<String,List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Mockito.when(submitCollectionReportHelperService.isBarcodeAlreadyAdded("123456",submitCollectionReportInfoMap)).thenReturn(false);
        Mockito.when(submitCollectionReportHelperService.isBarcodeAlreadyAdded(itemEntityList.get(0).getBarcode(),submitCollectionReportInfoMap)).thenReturn(false);
        submitCollectionDAOService.prepareExceptionReport(Arrays.asList("123456"),fetchedItemBarcodeList,incomingBarcodeItemEntityMapFromBibliographicEntityList,submitCollectionReportInfoMap);
    }

    @Test
    public  void updateDummyRecordEmpty(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity("1577261074");
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List< ItemChangeLogEntity > itemChangeLogEntityList = new ArrayList<>();
        ItemChangeLogEntity itemChangeLogEntity = getItemChangeLogEntity();
        itemChangeLogEntityList.add(itemChangeLogEntity);
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("23");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(new ItemEntity());
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(bibliographicEntity.getItemEntities())).thenReturn(itemEntities);
        BibliographicEntity bibliographicEntity1 = submitCollectionDAOService.updateDummyRecord(bibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity);
        assertNotNull(bibliographicEntity1);
    }

    @Test
    public  void updateDummyRecord(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity("1577261074");
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List< ItemChangeLogEntity > itemChangeLogEntityList = new ArrayList<>();
        ItemChangeLogEntity itemChangeLogEntity = getItemChangeLogEntity();
        itemChangeLogEntityList.add(itemChangeLogEntity);
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
        List<ItemEntity> itemEntities = getBibliographicEntity("1577261074").getItemEntities();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(bibliographicEntity.getItemEntities())).thenReturn(itemEntities);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(repositoryService.getItemChangeLogDetailsRepository()).thenReturn(itemChangeLogDetailsRepository);
        Mockito.doNothing().when(bibliographicDetailsRepository).delete(fetchBibliographicEntity);
        Mockito.doNothing().when(bibliographicDetailsRepository).flush();
        Mockito.when(bibliographicDetailsRepository.saveAndFlush(bibliographicEntity)).thenReturn(bibliographicEntity);
        Mockito.when(repositoryService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId())).thenReturn(bibliographicEntity);
        Mockito.doNothing().when(entityManager).refresh(bibliographicEntity);
        BibliographicEntity bibliographicEntity1 = submitCollectionDAOService.updateDummyRecord(bibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity);
        assertNotNull(bibliographicEntity1);
    }

    @Test
    public  void updateDummyRecordWithoutCollectionGroupId(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity2("1577261074");
        bibliographicEntity.getItemEntities().get(0).setCollectionGroupId(null);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
        List<ItemEntity> itemEntities = getBibliographicEntity("1577261074").getItemEntities();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(bibliographicEntity.getItemEntities())).thenReturn(itemEntities);
        BibliographicEntity bibliographicEntity1 = submitCollectionDAOService.updateDummyRecord(bibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity);
        assertNotNull(bibliographicEntity1);
    }
    @Test
    public void updateDummyRecordForNonBoundWith(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity("1577261074");
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
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
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity("1577261074");
        incomingBibliographicEntity.getItemEntities().get(0).setCollectionGroupId(null);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(incomingBibliographicEntity.getItemEntities())).thenReturn(Collections.EMPTY_LIST);
        submitCollectionDAOService.updateDummyRecordForNonBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList);
    }
    @Test
    public void updateDummyRecordForNonBoundWithfetchedItemBasedOnOwningInstitutionItemId(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity("1577261074");
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        List<ItemEntity> fetchedItemBasedOnOwningInstitutionItemId = getBibliographicEntity("1577261074").getItemEntities();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(incomingBibliographicEntity.getItemEntities())).thenReturn(fetchedItemBasedOnOwningInstitutionItemId);
        submitCollectionDAOService.updateDummyRecordForNonBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList);
    }

    @Test
    public void updateDummyRecordForBoundWith(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity2("1577261074");
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
        List< ItemChangeLogEntity > itemChangeLogEntityList = new ArrayList<>();
        boolean deleteDummyRecord = true;
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(123456);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.doNothing().when(bibliographicDetailsRepository).delete(fetchBibliographicEntity);
        Mockito.doNothing().when(bibliographicDetailsRepository).flush();
        Mockito.when(repositoryService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(incomingBibliographicEntity.getOwningInstitutionId(), incomingBibliographicEntity.getOwningInstitutionBibId())).thenReturn(getBibliographicEntity("1577261074"));
        BibliographicEntity bibliographicEntity = submitCollectionDAOService.updateDummyRecordForBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList,deleteDummyRecord,processedBibIds);
        assertNotNull(bibliographicEntity);
    }
    @Test
    public void updateDummyRecordForBoundWithoutCollectionGroupId(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity("1577261074");
        incomingBibliographicEntity.getItemEntities().get(0).setCollectionGroupId(null);
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSet = new HashSet<>();
        processedBarcodeSet.add("123456");
        BibliographicEntity savedBibliographicEntity = getBibliographicEntity("1577261074");
        BibliographicEntity fetchBibliographicEntity = getBibliographicEntity("1577261074");
        List< ItemChangeLogEntity > itemChangeLogEntityList = new ArrayList<>();
        boolean deleteDummyRecord = true;
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(123456);
        BibliographicEntity bibliographicEntity = submitCollectionDAOService.updateDummyRecordForBoundWith(incomingBibliographicEntity,submitCollectionReportInfoMap,idMapToRemoveIndexList,processedBarcodeSet,savedBibliographicEntity,fetchBibliographicEntity,itemChangeLogEntityList,deleteDummyRecord,processedBibIds);
        assertNotNull(bibliographicEntity);
    }
    private BoundWithBibliographicEntityObject getBoundWithBibliographicEntityObject(){
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = new BoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity2("1577261074");
        boundWithBibliographicEntityObject.setBarcode("123456");
        boundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        return boundWithBibliographicEntityObject;
    }
    private NonBoundWithBibliographicEntityObject getNonBoundWithBibliographicEntityObject(String OwningInstitutionBibId){
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = new NonBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(OwningInstitutionBibId);
        nonBoundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        nonBoundWithBibliographicEntityObject.setOwningInstitutionBibId(OwningInstitutionBibId);
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

    private BibliographicEntity getBibliographicEntity(String OwningInstitutionBibId){

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(4);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(OwningInstitutionBibId);
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCatalogingStatus("inComplete");

        List<ItemEntity> itemEntities = new ArrayList<>();
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
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
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        holdingsEntity.setItemEntities(itemEntities);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        bibliographicEntity.setItemEntities(itemEntities);

        return bibliographicEntity;
    }

    private BibliographicEntity getBibliographicEntityBoundwith(String OwningInstitutionBibId,String OwningInstitutionHoldingsId){

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(4);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(OwningInstitutionBibId);
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCatalogingStatus("inComplete");

        BibliographicEntity bibliographicEntity1 =bibliographicEntity;
        bibliographicEntity.setOwningInstitutionBibId("64343");

        List<ItemEntity> itemEntities = new ArrayList<>();
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
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
        holdingsEntity.setOwningInstitutionHoldingsId(OwningInstitutionHoldingsId);
        holdingsEntity.setDeleted(false);
        //holdingsEntity.setItemEntities(itemEntities);
        holdingsEntities.add(holdingsEntity);


        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity,bibliographicEntity1));
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        holdingsEntity.setItemEntities(itemEntities);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        bibliographicEntity.setItemEntities(itemEntities);

        return bibliographicEntity;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }

    private BibliographicEntity getBibliographicEntity2(String OwningInstitutionBibId){

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(123456);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(OwningInstitutionBibId);
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
        itemEntity.setId(1);
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
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity("1577261074");
        bibliographicEntity.setOwningInstitutionBibId("34558");
        BibliographicEntity bibliographicEntity2= getBibliographicEntity("1577261074");
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

    private Set<Integer> getIntegers() {
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        return processedBibIds;
    }

}
