package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.junit.Ignore;
import org.junit.Test;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.TestUtil;
import org.recap.converter.AccessionXmlToBibEntityConverterInterface;
import org.recap.converter.XmlToBibEntityConverterFactory;
import org.recap.model.ILSConfigProperties;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.BibliographicRepositoryDAO;
import org.recap.service.accession.AccessionValidationService;
import org.recap.service.accession.DummyDataService;
import org.recap.service.common.SetupDataService;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

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
    BibliographicRepositoryDAO bibliographicRepositoryDAO;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    SetupDataService setupDataService;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    AccessionRequest accessionRequest;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Mock
    OwnerCodeEntity ownerCodeEntity;

    @Mock
    ImsLocationEntity imsLocationEntity;

    @Mock
    InstitutionEntity institutionEntity;


    @Test
    public void processAndValidateBibliographicEntitysuccessBibCount(){
        StringBuilder errorMessage=new StringBuilder();
        Mockito.when(marcUtil.getControlFieldValue(any(),Mockito.anyString())).thenReturn("111");
        Mockito.when(marcUtil.writeMarcXml(any())).thenReturn("test");
        Mockito.when(marcUtil.isSubFieldExists(any(Record.class),Mockito.anyString())).thenReturn(true);
        Mockito.when(bibRecord.getLeader()).thenReturn(leader);
        Mockito.when(leader.toString()).thenReturn("01750cam a2200493 i 4500");
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Mockito.anyInt(),Mockito.anyString())).thenReturn(null);
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,1,new Date(),errorMessage);
        assertEquals(1,map.get(ScsbCommonConstants.SUCCESS_BIB_COUNT));
    }

    @Test
    public void processAndValidateBibliographicEntityexitsBibCount(){
        StringBuilder errorMessage=new StringBuilder();
        Mockito.when(marcUtil.getControlFieldValue(any(),Mockito.anyString())).thenReturn("111");
        Mockito.when(marcUtil.writeMarcXml(any())).thenReturn("test");
        Mockito.when(marcUtil.isSubFieldExists(any(Record.class),Mockito.anyString())).thenReturn(true);
        Mockito.when(bibRecord.getLeader()).thenReturn(leader);
        Mockito.when(leader.toString()).thenReturn("01750cam a2200493 i 4500");
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Mockito.anyInt(),Mockito.anyString())).thenReturn(new BibliographicEntity());
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,1,new Date(),errorMessage);
        assertEquals(1,map.get(ScsbCommonConstants.EXIST_BIB_COUNT));
    }

    @Test
    public void testLeaderValue(){
        StringBuilder errorMessage=new StringBuilder();
        Mockito.when(marcUtil.getControlFieldValue(any(),Mockito.anyString())).thenReturn("111");
        Mockito.when(marcUtil.writeMarcXml(any())).thenReturn("test");
        Mockito.when(marcUtil.isSubFieldExists(any(Record.class),Mockito.anyString())).thenReturn(true);
        Mockito.when(bibRecord.getLeader()).thenReturn(leader);
        Mockito.when(leader.toString()).thenReturn("01750cam a2200493 i 4500 ");
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Mockito.anyInt(),Mockito.anyString())).thenReturn(new BibliographicEntity());
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,1,new Date(),errorMessage);
        assertEquals(1,map.get(ScsbCommonConstants.EXIST_BIB_COUNT));
    }

    @Test
    public void processAndValidateBibliographicEntityexitsBibCountfailedBibCount(){
        ReflectionTestUtils.setField(accessionUtil,"restTemplate",null);
        accessionUtil.getRestTemplate();
        StringBuilder errorMessage=new StringBuilder();
        Map<String, Object> map=accessionUtil.processAndValidateBibliographicEntity(bibRecord,null,new Date(),errorMessage);
        assertEquals(1,map.get(ScsbCommonConstants.FAILED_BIB_COUNT));
    }

    @Test
    public void getOwningInstitution() throws Exception {
        Mockito.when(imsLocationEntity.getId()).thenReturn(1);
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(Mockito.anyString())).thenReturn(imsLocationEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndImsLocationId(Mockito.anyString(),Mockito.anyInt())).thenReturn(ownerCodeEntity);
        Mockito.when(ownerCodeEntity.getInstitutionEntity()).thenReturn(institutionEntity);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn(ScsbCommonConstants.PRINCETON);
        String owningInstitution=accessionUtil.getOwningInstitution("PA", "RECAP");
        assertEquals(ScsbCommonConstants.PRINCETON,owningInstitution);
    }

    @Test
    public void getOwningInstitutionException() throws Exception {
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCode(Mockito.anyString())).thenThrow(NullPointerException.class);
        String owningInstitution=accessionUtil.getOwningInstitution("PA", "RECAP");
        assertNull(owningInstitution);
    }

    @Test
    public void createReportDataEntityList(){
        Mockito.when(accessionRequest.getCustomerCode()).thenReturn("PA");
        Mockito.when(accessionRequest.getItemBarcode()).thenReturn("123456");
        List<ReportDataEntity> reportDataEntityList=accessionUtil.createReportDataEntityList(accessionRequest,"");
        assertNotNull(reportDataEntityList);
    }

    private OwnerCodeEntity getOwnerCodeEntity() {
        OwnerCodeEntity ownerCodeEntity=new OwnerCodeEntity();
        ownerCodeEntity.setId(1);
        ownerCodeEntity.setInstitutionId(1);
        ownerCodeEntity.setDescription("Princeton");
        ownerCodeEntity.setOwnerCode("PUL");
        ownerCodeEntity.hashCode();
        new OwnerCodeEntity().hashCode();
        ownerCodeEntity.compareTo(new OwnerCodeEntity());
        ownerCodeEntity.compareTo(ownerCodeEntity);
        ownerCodeEntity.equals(new OwnerCodeEntity());
        ownerCodeEntity.equals(ownerCodeEntity);
        OwnerCodeEntity ownerCodeEntity1=new OwnerCodeEntity();
        ownerCodeEntity1.setId(1);
        ownerCodeEntity1.setOwnerCode("CU");
        ownerCodeEntity.equals(ownerCodeEntity1);
        OwnerCodeEntity ownerCodeEntity2=new OwnerCodeEntity();
        ownerCodeEntity2.setId(1);
        ownerCodeEntity2.setOwnerCode("PUL");
        ownerCodeEntity2.setDescription("Columbia");
        ownerCodeEntity.equals(ownerCodeEntity2);
        OwnerCodeEntity ownerCodeEntity3=new OwnerCodeEntity();
        ownerCodeEntity3.setId(1);
        ownerCodeEntity3.setOwnerCode("PUL");
        ownerCodeEntity3.setDescription("Princeton");
        ownerCodeEntity.setInstitutionId(8);
        ownerCodeEntity.equals(ownerCodeEntity3);
        boolean code= ownerCodeEntity.equals(null);
        ownerCodeEntity.equals(OwnerCodeEntity.class);
        assertFalse(code);
        InstitutionEntity institutionEntity=TestUtil.getInstitutionEntity(1,"PUL","princeton");
        ownerCodeEntity.setInstitutionEntity(institutionEntity);
        return ownerCodeEntity;
    }
    @Test
    public void reAccessionItem() throws Exception {
        Map<Integer, String> collection = new HashMap<>();
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(getBibliographicEntity1().getItemEntities().get(0));
        List<ReportDataEntity> reportDataEntities=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntities.add(reportDataEntity);
        Mockito.when(setupDataService.getCollectionGroupIdCodeMap()).thenReturn(collection);
        accessionUtil.saveReportEntity("",reportDataEntities);
       // Mockito.doReturn(ScsbCommonConstants.SHARED_CGD).when(setupDataService).getCollectionGroupIdCodeMap().get(0);
        String message=accessionUtil.reAccessionItem(itemEntityList);
        String messageIndexed=accessionUtil.indexReaccessionedItem(itemEntityList);
        assertNotNull(message);
        assertNotNull(messageIndexed);
    }


    @Test
    public void reAccessionItemException() throws Exception {
        List<ItemEntity> itemEntityList=new ArrayList<>();
        itemEntityList.add(getBibliographicEntity1().getItemEntities().get(0));
        Mockito.when(itemDetailsRepository.saveAll(Mockito.anyCollection())).thenThrow(NullPointerException.class);
        String message=accessionUtil.reAccessionItem(itemEntityList);
        String messageIndexed=accessionUtil.indexReaccessionedItem(null);
        assertEquals( ScsbCommonConstants.FAILURE,message);
        assertEquals( ScsbCommonConstants.FAILURE,messageIndexed);
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
        bibliographicEntity.setId(1);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Mockito.when(dummyDataService.createDummyDataAsIncomplete(Mockito.anyInt(),Mockito.anyString(),Mockito.anyString(), any())).thenReturn(bibliographicEntity);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        String message=accessionUtil.createDummyRecordIfAny(ScsbConstants.INVALID_MARC_XML_ERROR_MSG,"PUL",reportDataEntityList,accessionRequest,imsLocationEntity);
        assertNotNull(ScsbConstants.ACCESSION_DUMMY_RECORD,message);
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
        bibliographicEntity.setId(1);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Mockito.when(dummyDataService.createDummyDataAsIncomplete(null,null,null,imsLocationEntity)).thenReturn(bibliographicEntity);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        String message=accessionUtil.createDummyRecordIfAny(ScsbConstants.INVALID_MARC_XML_ERROR_MSG,"",reportDataEntityList,accessionRequest,imsLocationEntity);
        assertNotNull(ScsbConstants.ITEM_BARCODE_ALREADY_ACCESSIONED_MSG,message);
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
        bibliographicEntity.setId(1);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Mockito.when(dummyDataService.createDummyDataAsIncomplete(null,null,null,imsLocationEntity)).thenReturn(bibliographicEntity);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        String message=accessionUtil.createDummyRecordIfAny(ScsbConstants.INVALID_MARC_XML_ERROR_MSG,"",reportDataEntityList,accessionRequest,imsLocationEntity);
        assertNotNull(ScsbConstants.ACCESSION_DUMMY_RECORD,message);
    }

    @Test
    public void updateDataFailed(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
        assertEquals(ScsbConstants.FAILED,response);
    }

    @Test
    public void updateDataSuccess(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(any(),Mockito.anyString(), any(), any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(any(),Mockito.anyBoolean(),Mockito.anyBoolean(), any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity());
        Mockito.when(bibliographicRepositoryDAO.saveOrUpdate(any())).thenReturn(getBibliographicEntity());
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
        assertEquals(ScsbCommonConstants.SUCCESS,response);
    }

    @Test
    public void updateDataAccesionException(){
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(any(),Mockito.anyString(), any(), any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(any(),Mockito.anyBoolean(),Mockito.anyBoolean(), any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(getBibliographicEntity());
        Mockito.when(bibliographicRepositoryDAO.saveOrUpdate(any())).thenThrow(NullPointerException.class);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
        assertNull(response);
    }
    @Test
    public void updateDataForBoundwith() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.setMaQualifier(1);
        Map<Integer,String> collection = new HashMap<>();
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,bibliographicEntity);
        Mockito.when(converter.convert(any(),Mockito.anyString(), any(), any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(any(),Mockito.anyBoolean(),Mockito.anyBoolean(), any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(bibliographicEntity);
        Mockito.when(bibliographicRepositoryDAO.saveOrUpdate(any())).thenReturn(bibliographicEntity);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(setupDataService.getCollectionGroupIdCodeMap()).thenReturn(collection);
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
        assertNotNull(response);
    }

    @Test
    public void updateDataIncomplete() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntity.setMaQualifier(1);

        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,bibliographicEntity);
        responseMap.put(ScsbConstants.INCOMPLETE_RESPONSE,"test");
        Mockito.when(converter.convert(any(),Mockito.anyString(), any(), any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(any(),Mockito.anyBoolean(),Mockito.anyBoolean(), any())).thenReturn(true);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(bibliographicEntity);
        Mockito.when(bibliographicRepositoryDAO.saveOrUpdate(any())).thenReturn(bibliographicEntity);
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
        assertEquals(ScsbConstants.SUCCESS_INCOMPLETE_RECORD,response);
    }

    @Test
    public void update() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        Mockito.when(xmlToBibEntityConverterFactory.getConverter(Mockito.anyString())).thenReturn(converter);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(any(),Mockito.anyString(), any(), any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(any(),Mockito.anyBoolean(),Mockito.anyBoolean(), any())).thenReturn(false);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
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
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(any(),Mockito.anyString(), any(), any())).thenReturn(responseMap);
        Mockito.when(accessionValidationService.validateItemAndHolding(any(),Mockito.anyBoolean(),Mockito.anyBoolean(), any())).thenReturn(true);
        Mockito.when(bibliographicRepositoryDAO.saveOrUpdate(any())).thenReturn(getBibliographicEntity());
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), any(), any())).thenReturn(new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK));
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
        assertEquals(ScsbCommonConstants.SUCCESS,response);
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
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntity());
        Mockito.when(converter.convert(any(),Mockito.anyString(), any(), any())).thenReturn(responseMap);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        String response=accessionUtil.updateData(bibRecord,"",responseMapList,accessionRequest,true,true,imsLocationEntity);
        assertEquals(ScsbConstants.FAILED + ScsbCommonConstants.HYPHEN + stringBuilder.toString(),response);
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
        bibliographicEntity.setCatalogingStatus(ScsbCommonConstants.INCOMPLETE_STATUS);
        bibliographicEntity.setMaQualifier(1);
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
        itemEntity.setCatalogingStatus( ScsbCommonConstants.COMPLETE_STATUS);
        assertTrue(itemEntity.isComplete());
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
        bibliographicEntity.setId(1);
        bibliographicEntity.setCatalogingStatus(ScsbCommonConstants.INCOMPLETE_STATUS);
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(today);
        holdingsEntity.setLastUpdatedDate(today);
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionHoldingsId("1621");
        holdingsEntity.setId(1);
        holdingsEntity.hashCode();
        holdingsEntity.equals(new HoldingsEntity());
        holdingsEntity.equals(null);
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
        itemEntity.setId(1);
        assertFalse(itemEntity.isComplete());
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
        InstitutionEntity institutionEntity= TestUtil.getInstitutionEntity(1,"PUL","Princeton");
        institutionEntities.add(institutionEntity);
        return institutionEntities;
    }
}
