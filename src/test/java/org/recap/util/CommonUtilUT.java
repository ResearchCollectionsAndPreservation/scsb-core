package org.recap.util;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.*;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.repository.jpa.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.JAXBException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class CommonUtilUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    CommonUtil commonUtil;

    @Mock
    BibJSONUtil bibJSONUtil;

    @Mock
    Future future;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    MarcUtil marcUtil;

    @Mock
    AccessionUtil accessionUtil;

    @Mock
    Record record;

    @Mock
    PropertyUtil propertyUtil;

    @Value("${" + PropertyKeyConstants.SCSB_SUPPORT_INSTITUTION + "}")
    private String supportInstitution;

    private String scsbXmlContent = "<bibRecords>\n" +
            "    <bibRecord>\n" +
            "        <bib>\n" +
            "            <owningInstitutionId>NYPL</owningInstitutionId>\n" +
            "            <owningInstitutionBibId>.b100000186</owningInstitutionBibId>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                <record>\n" +
            "                    <controlfield tag=\"001\">NYPG001000008-B</controlfield>\n" +
            "                    <controlfield tag=\"005\">20001116192418.8</controlfield>\n" +
            "                    <controlfield tag=\"008\">841106s1975 le b 000 0 arax cam i</controlfield>\n" +
            "                    <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "                        <subfield code=\"a\">Bashsh.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"8\" ind2=\" \" tag=\"952\">\n" +
            "                        <subfield code=\"h\">*OFX 84-1995</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                        <subfield code=\"a\">Women</subfield>\n" +
            "                        <subfield code=\"z\">Lebanon.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"250\">\n" +
            "                        <subfield code=\"a\">al-Tah 1.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "                        <subfield code=\"a\">78970449</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "                        <subfield code=\"a\">Includes bibliographical references.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"546\">\n" +
            "                        <subfield code=\"a\">In Arabic.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "                        <subfield code=\"a\">Bayr:</subfield>\n" +
            "                        <subfield code=\"b\">Dr al-Tah,</subfield>\n" +
            "                        <subfield code=\"c\">1975.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"8\" ind2=\" \" tag=\"952\">\n" +
            "                        <subfield code=\"h\">*OFX 84-1995</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "                        <subfield code=\"a\">68 p. ;</subfield>\n" +
            "                        <subfield code=\"c\">20 cm.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"1\" ind2=\"3\" tag=\"245\">\n" +
            "                        <subfield code=\"a\">al-Marah al-Lubnyah :</subfield>\n" +
            "                        <subfield code=\"b\">wwa-qad/</subfield>\n" +
            "                        <subfield code=\"c\">NajlBashsh</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"959\">\n" +
            "                        <subfield code=\"a\">.b10000197</subfield>\n" +
            "                        <subfield code=\"b\">07-18-08</subfield>\n" +
            "                        <subfield code=\"c\">07-29-91</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "                        <subfield code=\"c\">NN</subfield>\n" +
            "                        <subfield code=\"d\">NN</subfield>\n" +
            "                        <subfield code=\"d\">WaOLN</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"043\">\n" +
            "                        <subfield code=\"a\">a-le---</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
            "                        <subfield code=\"a\">HQ1728</subfield>\n" +
            "                        <subfield code=\"b\">.B37</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"908\">\n" +
            "                        <subfield code=\"a\">HQ1728</subfield>\n" +
            "                        <subfield code=\"b\">.B37</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"997\">\n" +
            "                        <subfield code=\"a\">ho</subfield>\n" +
            "                        <subfield code=\"b\">12-15-00</subfield>\n" +
            "                        <subfield code=\"c\">m</subfield>\n" +
            "                        <subfield code=\"d\">a</subfield>\n" +
            "                        <subfield code=\"e\">-</subfield>\n" +
            "                        <subfield code=\"f\">ara</subfield>\n" +
            "                        <subfield code=\"g\">le</subfield>\n" +
            "                        <subfield code=\"h\">3</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"907\">\n" +
            "                        <subfield code=\"a\">.b100000186</subfield>\n" +
            "                    </datafield>\n" +
            "                    <leader>00777cam a2200229 i 4500</leader>\n" +
            "                </record>\n" +
            "            </collection>\n" +
            "        </content>\n" +
            "    </bib>\n" +
            "    <holdings>\n" +
            "        <holding>\n" +
            "            <owningInstitutionHoldingsId/>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                <record>\n" +
            "                    <datafield ind1=\"8\" ind2=\" \" tag=\"852\">\n" +
            "                        <subfield code=\"b\">rcma2</subfield>\n" +
            "                        <subfield code=\"h\">*OFX 84-1995</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"866\">\n" +
            "                        <subfield code=\"a\"/>\n" +
            "                    </datafield>\n" +
            "                </record>\n" +
            "            </collection>\n" +
            "        </content>\n" +
            "        <items>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                <record>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"876\">\n" +
            "                        <subfield code=\"p\">33433002031718</subfield>\n" +
            "                        <subfield code=\"h\">In Library Use</subfield>\n" +
            "                        <subfield code=\"a\">.i100000046</subfield>\n" +
            "                        <subfield code=\"j\">Available</subfield>\n" +
            "                        <subfield code=\"t\">1</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"900\">\n" +
            "                        <subfield code=\"a\">Shared</subfield>\n" +
            "                        <subfield code=\"b\">NA</subfield>\n" +
            "                    </datafield>\n" +
            "                </record>\n" +
            "            </collection>\n" +
            "        </content>\n" +
            "    </items>\n" +
            "</holding>\n" +
            "</holdings>\n" +
            "</bibRecord>\n" +
            "</bibRecords>\n";

    @Test
    public void buildHoldingsEntityEmpty(){
        StringBuilder errorMessage=new StringBuilder();
        HoldingsEntity holdingsEntity=commonUtil.buildHoldingsEntity(getBibliographicEntity(),new Date(),errorMessage,"","UT");
        assertNotNull(holdingsEntity);
        assertEquals(" Holdings Content cannot be empty",errorMessage.toString());
    }

    @Test
    public void buildHoldingsEntity(){
        StringBuilder errorMessage=new StringBuilder();
        HoldingsEntity holdingsEntity=commonUtil.buildHoldingsEntity(getBibliographicEntity(),new Date(),errorMessage,"test","UT");
        assertNotNull(holdingsEntity);
        assertEquals("",errorMessage.toString());
    }

    @Test
    public void rollbackUpdateItemAvailabilityStatus(){
        ItemStatusEntity itemStatusEntity=new ItemStatusEntity();
        itemStatusEntity.setId(1);
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(Mockito.anyString())).thenReturn(itemStatusEntity);
        commonUtil.rollbackUpdateItemAvailabilityStatus(getBibliographicEntity().getItemEntities().get(0),"test");
        commonUtil.rollbackUpdateItemAvailabilityStatus(getBibliographicEntity().getItemEntities().get(0),"");
        assertNotNull(itemStatusEntity);
    }

    @Test
    public void getBarcodesList(){
        List<String> itemBarcodes=commonUtil.getBarcodesList(Arrays.asList(getItemEntity()));
        assertEquals("123456",itemBarcodes.get(0));
    }

    @Test
    public void getAllInstitutionCodes(){
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(getInstitutionEntities());
        Map response1= commonUtil.getInstitutionEntityMap();
        assertNotNull(response1);
    }

    private List<CollectionGroupEntity> getCollectionGroupEntities() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntity.setId(1);
        List<CollectionGroupEntity> collectionGroupEntityList = new ArrayList<>();
        collectionGroupEntityList.add(collectionGroupEntity);
        return collectionGroupEntityList;
    }

    private List<InstitutionEntity> getInstitutionEntities() {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("Princeton");
        institutionEntity.setId(1);
        institutionEntities.add(institutionEntity);
        return institutionEntities;
    }

    @Test
    public void getCollectionGroupMap(){
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(getCollectionGroupEntities());
        Map response= commonUtil.getCollectionGroupMap();
        assertNotNull(response);

    }

    @Test
    public void getBibRecordsForSCSBFormat(){
        BibRecords bibRecords=commonUtil.getBibRecordsForSCSBFormat(scsbXmlContent);
        assertNotNull(bibRecords);
    }

    @Test
    public void getBibRecordsForSCSBFormatException() throws JAXBException {
        Mockito.doThrow(new JAXBException("Error")).when(commonUtil).extractBibRecords(null);
        BibRecords bibRecords=commonUtil.getBibRecordsForSCSBFormat(null);
        assertNull(bibRecords);
    }

    @Test
    public void checkIfImsItemStatusIsAvailableOrNotAvailable(){
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(Mockito.anyString(),Mockito.anyString())).thenReturn("IN");
        boolean status=commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable("RECAP","IN",true);
        assertTrue(status);
    }

    @Test
    public void checkIfImsItemStatusIsRequestableNotRetrievable(){
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(Mockito.anyString(),Mockito.anyString())).thenReturn("IN");
        boolean status=commonUtil.checkIfImsItemStatusIsRequestableNotRetrievable("RECAP","IN");
        assertTrue(status);
    }

    @Test
    public void checkIfMatchPointsChanged(){
        String nonHoldingIdInstitution = "PUL,CUL,HTC,HD";
        ReflectionTestUtils.setField(commonUtil,"nonHoldingIdInstitution",nonHoldingIdInstitution);
        List<Record> incomingMarcRecords = new ArrayList<>();
        incomingMarcRecords.add(record);
        Mockito.when(marcUtil.convertMarcXmlToRecord(any())).thenReturn(incomingMarcRecords);
        boolean status = commonUtil.checkIfMatchPointsChanged("MarcRecord.xml","MarcRecord.xml","PUL");
        assertFalse(status);
    }

    @Test
    public void isCgdChangedToShared(){
        ItemEntity itemEntity = getItemEntity();
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("1",itemEntity);
        Map<String, ItemEntity> incomingBarcodeItemEntityMap = new HashMap<>();
        incomingBarcodeItemEntityMap.put("1",itemEntity);
        Map<Integer, String> collectionGroupIdCodeMap = new HashMap<>();
        collectionGroupIdCodeMap.put(1,ScsbCommonConstants.SHARED_CGD);
        Map<Integer, String> itemStatusIdCodeMap = new HashMap<>();
        itemStatusIdCodeMap.put(1,"AVAILABLE");
        boolean checkExisting = true;
        boolean status = commonUtil.isCgdChangedToShared(fetchedBarcodeItemEntityMap,incomingBarcodeItemEntityMap,collectionGroupIdCodeMap,itemStatusIdCodeMap,checkExisting);
        assertTrue(status);
    }

    @Test
    public void isCgdChangedToSharedAsNull(){
        ItemEntity itemEntity = getItemEntity();
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("1",itemEntity);
        Map<String, ItemEntity> incomingBarcodeItemEntityMap = new HashMap<>();
        incomingBarcodeItemEntityMap.put("1",itemEntity);
        Map<Integer, String> collectionGroupIdCodeMap = new HashMap<>();
        collectionGroupIdCodeMap.put(1,ScsbCommonConstants.AVAILABLE);
        Map<Integer, String> itemStatusIdCodeMap = new HashMap<>();
        itemStatusIdCodeMap.put(1,"AVAILABLE");
        boolean checkExisting = true;
        boolean status = commonUtil.isCgdChangedToShared(fetchedBarcodeItemEntityMap,incomingBarcodeItemEntityMap,collectionGroupIdCodeMap,itemStatusIdCodeMap,checkExisting);
        assertFalse(status);
    }

    @Test
    public void isCgdAlreadyShared(){
        ItemEntity itemEntity = getItemEntity();
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("1",itemEntity);
        Map<String, ItemEntity> incomingBarcodeItemEntityMap = new HashMap<>();
        incomingBarcodeItemEntityMap.put("1",itemEntity);
        Map<Integer, String> collectionGroupIdCodeMap = new HashMap<>();
        collectionGroupIdCodeMap.put(1,ScsbCommonConstants.SHARED_CGD);
        Map<Integer, String> itemStatusIdCodeMap = new HashMap<>();
        itemStatusIdCodeMap.put(1,"AVAILABLE");
        boolean status = commonUtil.isCgdAlreadyShared(fetchedBarcodeItemEntityMap,incomingBarcodeItemEntityMap,collectionGroupIdCodeMap,itemStatusIdCodeMap);
        assertTrue(status);
    }

    @Test
    public void collectSharedAndNonSharedBibIdsForMatchingId(){
        Set<Integer> sharedBibIds = new HashSet<>();
        Set<Integer> nonSharedBibIds = new HashSet<>();
        String matchingIdentifier = "1";
        Map<Integer, String> collectionGroupIdCodeMap = new HashMap<>();
        collectionGroupIdCodeMap.put(1,ScsbCommonConstants.SHARED_CGD);
        List<Object[]> bibIdAndCgdIdByMatchingIdentityObjectList = new ArrayList<>();
        Object[] bibliographicEntity = {1,2};
        bibIdAndCgdIdByMatchingIdentityObjectList.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.findBibIdAndCgdIdByMatchingIdentity(matchingIdentifier)).thenReturn(bibIdAndCgdIdByMatchingIdentityObjectList);
        commonUtil.collectSharedAndNonSharedBibIdsForMatchingId(sharedBibIds,nonSharedBibIds,matchingIdentifier,collectionGroupIdCodeMap);
    }

    @Test
    public void collectFuturesAndUpdateMAQualifier() throws ExecutionException, InterruptedException {
        List<Future> futures = new ArrayList<>();
        futures.add(future);
        Set<Integer> integerSet = new HashSet<>();
        integerSet.add(1);
        Map<Integer, Set<Integer>> responseMap = new HashMap<>();
        responseMap.put(ScsbCommonConstants.MA_QUALIFIER_1,integerSet);
        responseMap.put(ScsbCommonConstants.MA_QUALIFIER_2,integerSet);
        responseMap.put(ScsbCommonConstants.MA_QUALIFIER_3,integerSet);
        Mockito.when(future.get()).thenReturn(responseMap);
        commonUtil.collectFuturesAndUpdateMAQualifier(futures);
    }

    @Test
    public void collectFuturesAndUpdateMAQualifierException() throws ExecutionException, InterruptedException {
        List<Future> futures = new ArrayList<>();
        futures.add(future);
        Set<Integer> integerSet = new HashSet<>();
        integerSet.add(1);
        Map<Integer, Set<Integer>> responseMap = new HashMap<>();
        responseMap.put(ScsbCommonConstants.MA_QUALIFIER_1,null);
        responseMap.put(ScsbCommonConstants.MA_QUALIFIER_2,integerSet);
        responseMap.put(ScsbCommonConstants.MA_QUALIFIER_3,integerSet);
        Mockito.when(future.get()).thenReturn(responseMap);
        commonUtil.collectFuturesAndUpdateMAQualifier(futures);
    }

    @Test
    public void marcRecordConvert(){
        List<Record> records=new ArrayList<>();
        Mockito.when(marcUtil.readMarcXml(Mockito.anyString())).thenReturn(records);
        Object marcRecordConvert=commonUtil.marcRecordConvert("test");
        assertNotNull(marcRecordConvert);
    }


    @Test
    public void getItemStatusMapException(){
        Mockito.when(itemStatusDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        commonUtil.getItemStatusMap();
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        commonUtil.getCollectionGroupMap();
        assertNotNull(commonUtil);
    }

    @Test
    public void getUpdatedDataResponse(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        Set<AccessionResponse> accessionResponsesList=new HashSet<>();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(accessionUtil.updateData(any(),Mockito.anyString(),Mockito.anyList(), any(),Mockito.anyBoolean(),Mockito.anyBoolean(), any())).thenReturn(ScsbCommonConstants.SUCCESS);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        String updatedDataResponse=commonUtil.getUpdatedDataResponse(accessionResponsesList,responseMapList,"",reportDataEntityList,accessionRequest,true,1,record,imsLocationEntity);
        assertEquals(ScsbCommonConstants.SUCCESS,updatedDataResponse);
    }

    @Test
    public void getAllInstitutionCodesException(){
        Mockito.when(institutionDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        commonUtil.getInstitutionEntityMap();
        assertNotNull(commonUtil);
    }

    @Test
    public void updateMAQualifierByBibIdSets(){
        Set<Integer> allBibIdsToResetAndSetQualifierTo1 = new HashSet<>();
        allBibIdsToResetAndSetQualifierTo1.add(1);
        Set<Integer> allBibIdsToSetQualifierTo2 = new HashSet<>();
        allBibIdsToSetQualifierTo2.add(3);
        Set<Integer> allBibIdsToResetAndSetQualifierTo3 = new HashSet<>();
        allBibIdsToResetAndSetQualifierTo3.add(1);
        ReflectionTestUtils.invokeMethod(commonUtil,"updateMAQualifierByBibIdSets",allBibIdsToResetAndSetQualifierTo1,allBibIdsToSetQualifierTo2,allBibIdsToResetAndSetQualifierTo3);
    }
    @Test
    public void addHoldingsEntityToMap(){
        HoldingsEntity holdingsEntity=new HoldingsEntity();
        String owningid="fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234fgrtf-1234";
        Map<String, Object> response=commonUtil.addHoldingsEntityToMap(new HashMap<>(),holdingsEntity,"");
        Map<String, Object> response1=commonUtil.addHoldingsEntityToMap(new HashMap<>(),holdingsEntity,owningid);
        assertEquals(holdingsEntity,response1.get("holdingsEntity"));
    }

    @Test
    public void addItemAndReportEntities(){
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=getBibliographicEntity().getItemEntities().get(0);
        itemEntities.add(itemEntity);
        List<ReportEntity> reportEntities=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        reportEntities.add(reportEntity);
        Map<String, Object> itemMap=new HashMap<>();
        itemMap.put("itemEntity",getBibliographicEntity().getItemEntities().get(0));
        commonUtil.addItemAndReportEntities(itemEntities,reportEntities,true,getHoldingsEntity(),itemMap);
        Map<String, Object> itemMap1=new HashMap<>();
        itemMap1.put("itemReportEntity",reportEntity);
        itemMap1.put("itemEntity",getBibliographicEntity().getItemEntities().get(0));
        commonUtil.addItemAndReportEntities(itemEntities,reportEntities,true,getHoldingsEntity(),itemMap1);
        assertNotNull(itemMap);
    }

    @Test
    public void buildSubmitCollectionReportInfoAndAddFailuresForUnmatchedOwningInstitutionItemId(){
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        List<SubmitCollectionReportInfo > failureSubmitCollectionReportInfoList = new ArrayList<>();
        failureSubmitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        String owningInstitution = "PUL";
        Map<String, ItemEntity > itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",getBibliographicEntity().getItemEntities().get(0));
        Map.Entry<String, Map<String, ItemEntity >> incomingHoldingItemMapEntry = new AbstractMap.SimpleEntry<String, Map<String, ItemEntity>>("1", itemEntityMap);
        ItemEntity incomingItemEntity = new ItemEntity();
        commonUtil.buildSubmitCollectionReportInfoAndAddFailures(fetchedBibliographicEntity,failureSubmitCollectionReportInfoList,owningInstitution,incomingHoldingItemMapEntry,incomingItemEntity);
        assertNotNull(incomingItemEntity);
    }

    @Test
    public void getItemStatusMap(){
        List<ItemStatusEntity> itemStatusEntities = new ArrayList<>();
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("SUCCESS");
        itemStatusEntity.setStatusDescription("AVAILABLE");
        itemStatusEntities.add(itemStatusEntity);
        Mockito.when(itemStatusDetailsRepository.findAll()).thenReturn(itemStatusEntities);
        commonUtil.getItemStatusMap();
        assertNotNull(itemStatusEntities);
    }

    private SubmitCollectionReportInfo getSubmitCollectionReportInfo(){
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setOwningInstitution("PUL");
        submitCollectionReportInfo.setItemBarcode("123456");
        submitCollectionReportInfo.setCustomerCode("PA");
        submitCollectionReportInfo.setMessage("SUCCESS");
        return submitCollectionReportInfo;
    }
    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("12345");
        holdingsEntity.setDeleted(false);
        return holdingsEntity;
    }
    private BibliographicEntity getBibliographicEntity(){

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(123456);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1577261074");
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = getItemEntity();
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    private ItemEntity getItemEntity() {
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
        return itemEntity;
    }

    @Test
    public void findAllInstitutionCodesExceptSupportInstitution() {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodesExceptSupportInstitution(supportInstitution)).thenReturn(Collections.singletonList(institutionEntity.getInstitutionName()));
        List<String> result = commonUtil.findAllInstitutionCodesExceptSupportInstitution();
        assertNotNull(result);
    }

    @Test
    public void findAllInstitutionsExceptSupportInstitution() {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findAllInstitutionsExceptSupportInstitution(supportInstitution)).thenReturn(Collections.singletonList(institutionEntity));
        List<InstitutionEntity> result = commonUtil.findAllInstitutionsExceptSupportInstitution();
        assertNotNull(result);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        return institutionEntity;
    }
}
