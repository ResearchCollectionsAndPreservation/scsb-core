package org.recap.service.submitcollection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.common.SetupDataService;
import org.recap.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionValidationServiceUT{
    private static final Logger logger = LoggerFactory.getLogger(SubmitCollectionValidationService.class);
    @InjectMocks
    SubmitCollectionValidationService submitCollectionValidationService;
    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;
    @Mock
    SubmitCollectionHelperService submitCollectionHelperService;
    @Mock
    SetupDataService setupDataService;
    @Mock
    CommonUtil commonUtil;

    @Mock
    SubmitCollectionReportHelperService submitCollectionReportHelperService;
    @Before
    public void setUp() {
        ReflectionTestUtils.setField(submitCollectionValidationService, "nonHoldingIdInstitution", "NYPL");
    }
    @Test
    public void validateInstitution(){
        String institutionCode = "PUL";
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(institutionCode)).thenReturn(institutionEntity);
        boolean result = submitCollectionValidationService.validateInstitution(institutionCode);
        assertEquals(true,result);
    }
    @Test
    public void validateIncomingEntities(){
        Map<String,List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(5,"Available");
        institutionEntityMap.put(1,"PUL");
        Map<String,Map<String,ItemEntity>> holdingsItemMap = new HashMap<>();
        Map<String,ItemEntity> itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",getItemEntity());
        holdingsItemMap.put("1",itemEntityMap);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(fetchedBibliographicEntity)).thenReturn(holdingsItemMap);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(incomingBibliographicEntity)).thenReturn(holdingsItemMap);
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(incomingBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        boolean result = submitCollectionValidationService.validateIncomingEntities(submitCollectionReportInfoMap,fetchedBibliographicEntity,incomingBibliographicEntity);
        assertTrue(result);
    }
    @Test
    public void validateIncomingEntitiesForNYPL(){
        Map<String,List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(5,"Available");
        institutionEntityMap.put(1,"NYPL");
        Map<String,Map<String,ItemEntity>> holdingsItemMap = new HashMap<>();
        Map<String,ItemEntity> itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",getItemEntity());
        holdingsItemMap.put("1",itemEntityMap);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(fetchedBibliographicEntity)).thenReturn(holdingsItemMap);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(incomingBibliographicEntity)).thenReturn(holdingsItemMap);
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(incomingBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        boolean result = submitCollectionValidationService.validateIncomingEntities(submitCollectionReportInfoMap,fetchedBibliographicEntity,incomingBibliographicEntity);
        assertTrue(result);
    }
    @Test
    public void validateIncomingEntitiesWithoutFetchedHoldingsItemId(){
        Map<String,List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList = new ArrayList<>();
        failureSubmitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(5,"Available");
        institutionEntityMap.put(1,"PUL");
        Map<String,Map<String,ItemEntity>> holdingsItemMap = new HashMap<>();
        Map<String,ItemEntity> itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",getItemEntity());
        holdingsItemMap.put("1",itemEntityMap);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(fetchedBibliographicEntity)).thenReturn(Collections.EMPTY_MAP);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(incomingBibliographicEntity)).thenReturn(holdingsItemMap);
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(incomingBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        String owningInstitution = "NYPL";
        Map<String,ItemEntity> entityMap = new HashMap<>();
        //Map.Entry<String,Map<String,ItemEntity>> incomingHoldingItemMapEntry = new E<String,Map<String,ItemEntity>>();
//        Mockito.doNothing().when(commonUtil).buildSubmitCollectionReportInfoAndAddFailures(fetchedBibliographicEntity, failureSubmitCollectionReportInfoList, owningInstitution,null, getItemEntity());
        boolean result = submitCollectionValidationService.validateIncomingEntities(submitCollectionReportInfoMap,fetchedBibliographicEntity,incomingBibliographicEntity);
        assertFalse(result);
    }
    @Test
    public void validateIncomingEntitiesWithoutFetchedHoldingsItemIdAndCollectionGroupId(){
        Map<String,List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList = new ArrayList<>();
        failureSubmitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCollectionGroupId(null);
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(5,"Available");
        institutionEntityMap.put(1,"PUL");
        Map<String,Map<String,ItemEntity>> holdingsItemMap = new HashMap<>();
        Map<String,ItemEntity> itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",itemEntity);
        holdingsItemMap.put("1",itemEntityMap);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(fetchedBibliographicEntity)).thenReturn(Collections.EMPTY_MAP);
        Mockito.when(submitCollectionHelperService.getHoldingItemIdMap(incomingBibliographicEntity)).thenReturn(holdingsItemMap);
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(incomingBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        String owningInstitution = "NYPL";
        ItemEntity incomingItemEntity = getItemEntity();
//        Mockito.doNothing().when(commonUtil).buildSubmitCollectionReportInfoWhenNoGroupIdAndAddFailures(incomingBibliographicEntity, failureSubmitCollectionReportInfoList, owningInstitution, incomingItemEntity);
        boolean result = submitCollectionValidationService.validateIncomingEntities(submitCollectionReportInfoMap,fetchedBibliographicEntity,incomingBibliographicEntity);
        assertFalse(result);
    }
    @Test
    public void getOwningBibIdOwnInstHoldingsIdIfAnyHoldingMismatch(){
        List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
        bibliographicEntityList.add(getBibliographicEntity());
        List<String> holdingsIdUniqueList = new ArrayList<>();
        holdingsIdUniqueList.add("12345");
        Map<String,String> stringMap = submitCollectionValidationService.getOwningBibIdOwnInstHoldingsIdIfAnyHoldingMismatch(bibliographicEntityList,holdingsIdUniqueList);
        assertNotNull(stringMap);
    }
    @Test
    public  void validateIncomingItemHavingBibCountIsSameAsExistingItem(){
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList = new ArrayList<>();
        failureSubmitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(5,"Available");
        institutionEntityMap.put(1,"NYPL");
        Map<String,Map<String,ItemEntity>> holdingsItemMap = new HashMap<>();
        Map<String,ItemEntity> itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",getItemEntity());
        holdingsItemMap.put("1",itemEntityMap);
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(incomingBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getItemEntity());
        List<BibliographicEntity> incomingBibliographicEntityList = new ArrayList<>();
        incomingBibliographicEntityList.add(getBibliographicEntity());
        boolean result = submitCollectionValidationService.validateIncomingItemHavingBibCountIsSameAsExistingItem(submitCollectionReportInfoMap,fetchedBarcodeItemEntityMap,incomingBibliographicEntityList);
        assertTrue(result);
    }
    @Test
    public  void validateIncomingItemHavingBibCountIsSameAsExistingItemWithMatching(){
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList = new ArrayList<>();
        failureSubmitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(5,"Available");
        institutionEntityMap.put(1,"NYPL");
        Map<String,Map<String,ItemEntity>> holdingsItemMap = new HashMap<>();
        Map<String,ItemEntity> itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",getItemEntity());
        holdingsItemMap.put("1",itemEntityMap);
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getItemEntity());
        List<BibliographicEntity> incomingBibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("897546");
        incomingBibliographicEntityList.add(bibliographicEntity);
        ItemEntity incomingItemEntity = getItemEntity();
        Mockito.when(submitCollectionReportHelperService.isBarcodeAlreadyAdded(incomingItemEntity.getBarcode(),submitCollectionReportInfoMap)).thenReturn(false);
        Mockito.when(submitCollectionReportHelperService.isBarcodeAlreadyAdded(incomingItemEntity.getBarcode(),submitCollectionReportInfoMap)).thenReturn(false);
        boolean result = submitCollectionValidationService.validateIncomingItemHavingBibCountIsSameAsExistingItem(submitCollectionReportInfoMap,fetchedBarcodeItemEntityMap,incomingBibliographicEntityList);
        assertFalse(result);
    }
    @Test
    public void validateIncomingItemHavingBibCountGreaterThanExistingItem(){
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        List<BibliographicEntity> incomingBibliographicEntityList = new ArrayList<>();
        incomingBibliographicEntityList.add(getBibliographicEntity());
        List<BibliographicEntity> existingBibliographicEntityList = new ArrayList<>();
        existingBibliographicEntityList.add(getBibliographicEntity());
        boolean result = submitCollectionValidationService.validateIncomingItemHavingBibCountGreaterThanExistingItem(submitCollectionReportInfoMap,incomingBibliographicEntityList,existingBibliographicEntityList);
        assertTrue(result);
    }
    @Test
    public void validateIncomingItemHavingBibCountGreaterThanExistingItem2(){
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionSuccessList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionRejectionList",Arrays.asList(getSubmitCollectionReportInfo()));
        submitCollectionReportInfoMap.put("submitCollectionFailureList",Arrays.asList(getSubmitCollectionReportInfo()));
        List<BibliographicEntity> incomingBibliographicEntityList = new ArrayList<>();
        incomingBibliographicEntityList.add(getBibliographicEntity());
        List<BibliographicEntity> existingBibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("67890");
        existingBibliographicEntityList.add(bibliographicEntity);
        boolean result = submitCollectionValidationService.validateIncomingItemHavingBibCountGreaterThanExistingItem(submitCollectionReportInfoMap,incomingBibliographicEntityList,existingBibliographicEntityList);
        assertFalse(result);
    }
    @Test
    public void validateIncomingItemHavingBibCountLesserThanExistingItem(){
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        List<BibliographicEntity> incomingBibliographicEntityList = new ArrayList<>();
        incomingBibliographicEntityList.add(getBibliographicEntity());
        List<BibliographicEntity> existingBibliographicEntityList = new ArrayList<>();
        existingBibliographicEntityList.add(getBibliographicEntity());
        List<String> incomingBibsNotInExistingBibs = new ArrayList<>();
        incomingBibsNotInExistingBibs.add("23567");
        ItemEntity existingItemEntity = getItemEntity();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(1,"Available");
        institutionEntityMap.put(2,"PUL");
        Mockito.when(setupDataService.getItemStatusIdCodeMap().get(1)).thenReturn(institutionEntityMap);
        boolean result = submitCollectionValidationService.validateIncomingItemHavingBibCountLesserThanExistingItem(submitCollectionReportInfoMap,incomingBibliographicEntityList,existingBibliographicEntityList,incomingBibsNotInExistingBibs,existingItemEntity);
        assertFalse(result);
    }
    @Test
    public void validateIncomingItemHavingBibCountLesserThanExistingItemAvailable(){
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        List<BibliographicEntity> incomingBibliographicEntityList = new ArrayList<>();
        incomingBibliographicEntityList.add(getBibliographicEntity());
        List<BibliographicEntity> existingBibliographicEntityList = new ArrayList<>();
        existingBibliographicEntityList.add(getBibliographicEntity());
        List<String> incomingBibsNotInExistingBibs = new ArrayList<>();
        incomingBibsNotInExistingBibs.add("23567");
        ItemEntity existingItemEntity = getItemEntity();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(2,"Available");
        institutionEntityMap.put(1,"PUL");
        Mockito.when(setupDataService.getItemStatusIdCodeMap().get(1)).thenReturn(institutionEntityMap);
        boolean result = submitCollectionValidationService.validateIncomingItemHavingBibCountLesserThanExistingItem(submitCollectionReportInfoMap,incomingBibliographicEntityList,existingBibliographicEntityList,incomingBibsNotInExistingBibs,existingItemEntity);
        assertFalse(result);
    }
    @Test
    public void validateIncomingItemHavingBibCountLesserThanExistingItemWithoutIncomingBibsNotInExistingBibs(){
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        List<BibliographicEntity> incomingBibliographicEntityList = new ArrayList<>();
        incomingBibliographicEntityList.add(getBibliographicEntity());
        List<BibliographicEntity> existingBibliographicEntityList = new ArrayList<>();
        existingBibliographicEntityList.add(getBibliographicEntity());
        List<String> incomingBibsNotInExistingBibs = new ArrayList<>();
        ItemEntity existingItemEntity = getItemEntity();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(1,"Available");
        institutionEntityMap.put(2,"PUL");
        Mockito.when(setupDataService.getItemStatusIdCodeMap().get(1)).thenReturn(institutionEntityMap);
        boolean result = submitCollectionValidationService.validateIncomingItemHavingBibCountLesserThanExistingItem(submitCollectionReportInfoMap,incomingBibliographicEntityList,existingBibliographicEntityList,incomingBibsNotInExistingBibs,existingItemEntity);
        assertTrue(result);
    }
    private BibliographicEntity getBibliographicEntity(){
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

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
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
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }
    private SubmitCollectionReportInfo getSubmitCollectionReportInfo(){
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setOwningInstitution("PUL");
        submitCollectionReportInfo.setItemBarcode("123456");
        submitCollectionReportInfo.setCustomerCode("PA");
        submitCollectionReportInfo.setMessage("SUCCESS");
        return submitCollectionReportInfo;
    }

    private ItemEntity getItemEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        ItemEntity itemEntity = new ItemEntity();
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
        itemEntity.setCatalogingStatus("Incomplete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setUseRestrictions("restrictions");
        itemEntity.setDeleted(false);
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setBibliographicEntities(Arrays.asList(getBibliographicEntity()));
        return itemEntity;
    }

    @Test
    public void test() {
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setItemBarcode("33433001888415");
        submitCollectionReportInfo.setCustomerCode("PB");
        submitCollectionReportInfo.setOwningInstitution("CUL");
        submitCollectionReportInfo.setMessage("Rejection record - Only use restriction and cgd not updated because the item is in use");
        List<SubmitCollectionReportInfo> submitCollectionReportInfoList = new ArrayList<>();
        submitCollectionReportInfoList.add(submitCollectionReportInfo);
        Map<String, List<SubmitCollectionReportInfo>> data = new HashMap<>();
        data.put("Test", submitCollectionReportInfoList);

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");


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
        itemEntity.setBarcode("6027");
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
        List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
        bibliographicEntityList.add(bibliographicEntity);
        List<String> listholdings = new ArrayList<>();
        listholdings.add("test1");
        listholdings.add("test2");
        boolean datatest = false;
        try {
            datatest = submitCollectionValidationService.validateIncomingEntities(data, bibliographicEntity, bibliographicEntity);
        } catch (Exception e) {
            logger.info("Exception" + e);
        }
        try {
            submitCollectionValidationService.getOwningBibIdOwnInstHoldingsIdIfAnyHoldingMismatch(bibliographicEntityList, listholdings);
        } catch (Exception e) {
            logger.info("Exception" + e);
        }
    }
}
