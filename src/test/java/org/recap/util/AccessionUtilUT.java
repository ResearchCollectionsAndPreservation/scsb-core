package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.converter.AccessionXmlToBibEntityConverterInterface;
import org.recap.converter.XmlToBibEntityConverterFactory;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.service.accession.AccessionDAO;
import org.recap.service.accession.AccessionValidationService;
import org.recap.service.accession.DummyDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AccessionUtilUT extends BaseTestCaseUT{

    @InjectMocks
    AccessionUtil accessionUtil;

    @Mock
    MarcUtil marcUtil;

    @Mock
    DummyDataService dummyDataService;

    @Mock
    Record bibRecord;

    @Mock
    Leader leader;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    DBReportUtil dbReportUtil;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    XmlToBibEntityConverterFactory xmlToBibEntityConverterFactory;

    @Mock
    AccessionXmlToBibEntityConverterInterface converter;

    @Mock
    RestTemplate restTemplate;

    @Mock
    AccessionValidationService accessionValidationService;

    @Mock
    AccessionDAO accessionDAO;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Test
    public void processAndValidateBibliographicEntitysuccessBibCount(){
        StringBuilder errorMessage=new StringBuilder();
        Mockito.when(marcUtil.getControlFieldValue(Mockito.any(),Mockito.anyString())).thenReturn("111");
        Mockito.when(marcUtil.writeMarcXml(Mockito.any())).thenReturn("test");
        Mockito.when(marcUtil.isSubFieldExists(Mockito.any(Record.class),Mockito.anyString())).thenReturn(true);
        Mockito.when(bibRecord.getLeader()).thenReturn(leader);
        Mockito.when(leader.toString()).thenReturn("01750cam a2200493 i 4500");
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Mockito.anyInt(),Mockito.anyString())).thenReturn(null);
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,1,new Date(),errorMessage);
        assertEquals(1,map.get(RecapCommonConstants.SUCCESS_BIB_COUNT));
    }

    @Test
    public void processAndValidateBibliographicEntityexitsBibCount(){
        StringBuilder errorMessage=new StringBuilder();
        Mockito.when(marcUtil.getControlFieldValue(Mockito.any(),Mockito.anyString())).thenReturn("111");
        Mockito.when(marcUtil.writeMarcXml(Mockito.any())).thenReturn("test");
        Mockito.when(marcUtil.isSubFieldExists(Mockito.any(Record.class),Mockito.anyString())).thenReturn(true);
        Mockito.when(bibRecord.getLeader()).thenReturn(leader);
        Mockito.when(leader.toString()).thenReturn("01750cam a2200493 i 4500");
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Mockito.anyInt(),Mockito.anyString())).thenReturn(new BibliographicEntity());
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,1,new Date(),errorMessage);
        assertEquals(1,map.get(RecapCommonConstants.EXIST_BIB_COUNT));
    }

    @Test
    public void testLeaderValue(){
        StringBuilder errorMessage=new StringBuilder();
        Mockito.when(marcUtil.getControlFieldValue(Mockito.any(),Mockito.anyString())).thenReturn("111");
        Mockito.when(marcUtil.writeMarcXml(Mockito.any())).thenReturn("test");
        Mockito.when(marcUtil.isSubFieldExists(Mockito.any(Record.class),Mockito.anyString())).thenReturn(true);
        Mockito.when(bibRecord.getLeader()).thenReturn(leader);
        Mockito.when(leader.toString()).thenReturn("01750cam a2200493 i 4500 ");
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Mockito.anyInt(),Mockito.anyString())).thenReturn(new BibliographicEntity());
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,1,new Date(),errorMessage);
        assertEquals(1,map.get(RecapCommonConstants.EXIST_BIB_COUNT));
    }

    @Test
    public void processAndValidateBibliographicEntityexitsBibCountfailedBibCount(){
        ReflectionTestUtils.setField(accessionUtil,"restTemplate",null);
        accessionUtil.getRestTemplate();
        StringBuilder errorMessage=new StringBuilder();
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,null,new Date(),errorMessage);
        assertEquals(1,map.get(RecapCommonConstants.FAILED_BIB_COUNT));
    }

    @Test
    public void getOwningInstitution() throws Exception {
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(Mockito.anyString())).thenReturn(getCustomerCodeEntity());
        String owningInstitution=accessionUtil.getOwningInstitution("PA");
        assertEquals("PUL",owningInstitution);
    }

    @Test
    public void getOwningInstitutionException() throws Exception {
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(Mockito.anyString())).thenThrow(NullPointerException.class);
        String owningInstitution=accessionUtil.getOwningInstitution("PA");
        assertNull(owningInstitution);
    }

    private CustomerCodeEntity getCustomerCodeEntity() {
        CustomerCodeEntity customerCodeEntity=new CustomerCodeEntity();
        customerCodeEntity.setId(1);
        customerCodeEntity.setOwningInstitutionId(1);
        customerCodeEntity.setDescription("Princeton");
        customerCodeEntity.setCustomerCode("PUL");
        customerCodeEntity.hashCode();
        customerCodeEntity.compareTo(new CustomerCodeEntity());
        customerCodeEntity.equals(new CustomerCodeEntity());
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        customerCodeEntity.setInstitutionEntity(institutionEntity);
        return customerCodeEntity;
    }

    @Test
    public void reAccessionItem() throws Exception {
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(getBibliographicEntity1().getItemEntities().get(0));
        List<ReportDataEntity> reportDataEntities=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntities.add(reportDataEntity);
        accessionUtil.saveReportEntity("",reportDataEntities);
       // accessionUtil.saveItemChangeLogEntity("","",itemEntityList);
        String message=accessionUtil.reAccessionItem(itemEntityList);
        String messageIndexed=accessionUtil.indexReaccessionedItem(itemEntityList);
        assertEquals( RecapCommonConstants.SUCCESS,message);
        assertEquals( RecapCommonConstants.SUCCESS,messageIndexed);
    }

    @Test
    public void reAccessionItemException() throws Exception {
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(getBibliographicEntity1().getItemEntities().get(0));
        Mockito.when(itemDetailsRepository.saveAll(Mockito.anyCollection())).thenThrow(NullPointerException.class);
        String message=accessionUtil.reAccessionItem(itemEntityList);
        String messageIndexed=accessionUtil.indexReaccessionedItem(null);
        assertEquals( RecapCommonConstants.FAILURE,message);
        assertEquals( RecapCommonConstants.FAILURE,messageIndexed);
    }

    @Test
    public void createDummyRecordIfAny(){
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntityList.add(reportDataEntity);
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("12345");
        List<ItemEntity> itemEntityList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntityList.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(Mockito.anyList())).thenReturn(itemEntityList);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(getInstitutionEntities());
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        Mockito.when(dummyDataService.createDummyDataAsIncomplete(Mockito.anyInt(),Mockito.anyString(),Mockito.anyString())).thenReturn(bibliographicEntity);

        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String message=accessionUtil.createDummyRecordIfAny(RecapConstants.INVALID_MARC_XML_ERROR_MSG,"PUL",reportDataEntityList,accessionRequest);
        assertNotNull(RecapConstants.ACCESSION_DUMMY_RECORD,message);
    }

    @Test
    public void createDummyRecordIfAnyAlreadyAccessioned(){
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntityList.add(reportDataEntity);
        AccessionRequest accessionRequest=new AccessionRequest();
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(getBibliographicEntity().getItemEntities().get(0));
        Mockito.when(itemDetailsRepository.findByBarcodeIn(Mockito.anyList())).thenReturn(itemEntityList);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(getInstitutionEntities());
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        Mockito.when(dummyDataService.createDummyDataAsIncomplete(null,null,null)).thenReturn(bibliographicEntity);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String message=accessionUtil.createDummyRecordIfAny(RecapConstants.INVALID_MARC_XML_ERROR_MSG,"",reportDataEntityList,accessionRequest);
        assertNotNull(RecapConstants.ITEM_BARCODE_ALREADY_ACCESSIONED_MSG,message);
    }

    @Test
    public void getInstitutionEntityMap(){
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntityList.add(reportDataEntity);
        AccessionRequest accessionRequest=new AccessionRequest();
        List<ItemEntity> itemEntityList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntityList.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(Mockito.anyList())).thenReturn(itemEntityList);
        Mockito.when(institutionDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        Mockito.when(dummyDataService.createDummyDataAsIncomplete(null,null,null)).thenReturn(bibliographicEntity);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String message=accessionUtil.createDummyRecordIfAny(RecapConstants.INVALID_MARC_XML_ERROR_MSG,"",reportDataEntityList,accessionRequest);
        assertNotNull(RecapConstants.ACCESSION_DUMMY_RECORD,message);
    }

    @Test
    public void updateDataFailed(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertEquals(RecapConstants.FAILED,response);
    }

    @Test
    public void updateDataSuccess(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(Mockito.any(),Mockito.anyString(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(Mockito.any(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertEquals(RecapCommonConstants.SUCCESS,response);
    }

    @Test
    public void updateDataAccesionException(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(Mockito.any(),Mockito.anyString(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(Mockito.any(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenThrow(NullPointerException.class);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertNull(response);
    }

    @Test
    public void updateDataForBoundwith() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(Mockito.any(),Mockito.anyString(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(Mockito.any(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity1());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertEquals(RecapCommonConstants.SUCCESS,response);
    }

    @Test
    public void updateDataIncomplete() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        responseMap.put(RecapConstants.INCOMPLETE_RESPONSE,"test");
        Mockito.when(converter.convert(Mockito.any(),Mockito.anyString(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(Mockito.any(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity1());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertEquals(RecapConstants.SUCCESS_INCOMPLETE_RECORD,response);
    }

    @Test
    public void update() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(Mockito.any(),Mockito.anyString(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(Mockito.any(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.any())).thenReturn(false);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertEquals("",response);
    }

    @Test
    public void updatedDataSuccessNonExistBib(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(Mockito.any(),Mockito.anyString(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(Mockito.any(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.any())).thenReturn(true);
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK));
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertEquals(RecapCommonConstants.SUCCESS,response);
    }

    @Test
    public void updateDataFailedWithMessage(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("test");
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(Mockito.any(),Mockito.anyString(),Mockito.any())).thenReturn(responseMap);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true);
        assertEquals(RecapConstants.FAILED + RecapCommonConstants.HYPHEN + stringBuilder.toString(),response);
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
        bibliographicEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));

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
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));

        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;
    }

    public BibliographicEntity getBibliographicEntity1() throws Exception {
        Date today = new Date();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1421");
        bibliographicEntity.setBibliographicId(1);
        bibliographicEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(today);
        holdingsEntity.setLastUpdatedDate(today);
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionHoldingsId("1621");
        holdingsEntity.setHoldingsId(1);
        holdingsEntity.hashCode();
        holdingsEntity.equals(new HoldingsEntity());
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
        itemEntity.setItemId(1);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));

        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;
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
}
