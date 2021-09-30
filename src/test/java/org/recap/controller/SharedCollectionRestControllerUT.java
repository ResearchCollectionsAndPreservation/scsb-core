package org.recap.controller;

import org.apache.camel.Exchange;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.accession.AccessionModelRequest;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.submitcollection.SubmitCollectionResponse;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.accession.AccessionService;
import org.recap.service.accession.BulkAccessionService;
import org.recap.service.common.RepositoryService;
import org.recap.service.common.SetupDataService;
import org.recap.service.submitcollection.SubmitCollectionBatchService;
import org.recap.service.submitcollection.SubmitCollectionDAOService;
import org.recap.service.submitcollection.SubmitCollectionService;
import org.recap.service.submitcollection.SubmitCollectionValidationService;
import org.recap.util.CommonUtil;
import org.recap.util.MarcUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by premkb on 26/12/16.
 */

public class SharedCollectionRestControllerUT extends BaseTestCaseUT {

    @InjectMocks
    private SharedCollectionRestController sharedCollectionRestController;

    @Mock
    AccessionService accessionService;

    @Mock
    private SubmitCollectionBatchService submitCollectionBatchService;

    @Mock
    private SetupDataService setupDataService;

    @Mock
    CommonUtil commonUtil;

    @Mock
    Exchange exchange;

    @Mock
    BulkAccessionService bulkAccessionService;

    @Mock
    SubmitCollectionValidationService validationService;

    @Mock
    RepositoryService repositoryService;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    MarcUtil marcUtil;

    @Mock
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Mock
    SubmitCollectionDAOService submitCollectionDAOService;

    @Mock
    SubmitCollectionService submitCollectionService;

    String updatedMarcXml = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +
            "<record>\n" +
            "<leader>01011cam a2200289 a 4500</leader>\n" +
            "<controlfield tag=\"001\">115115</controlfield>\n" +
            "<controlfield tag=\"005\">20160503221017.0</controlfield>\n" +
            "<controlfield tag=\"008\">820315s1982 njua b 00110 eng</controlfield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "<subfield code=\"a\">81008543</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "<subfield code=\"a\">0132858908</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "<subfield code=\"a\">(OCoLC)7555877</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "<subfield code=\"a\">(CStRLIN)NJPG82-B5675</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "<subfield code=\"9\">AAS9821TS</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\" \" tag=\"039\">\n" +
            "<subfield code=\"a\">2</subfield>\n" +
            "<subfield code=\"b\">3</subfield>\n" +
            "<subfield code=\"c\">3</subfield>\n" +
            "<subfield code=\"d\">3</subfield>\n" +
            "<subfield code=\"e\">3</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\" \" tag=\"050\">\n" +
            "<subfield code=\"a\">QE28.3</subfield>\n" +
            "<subfield code=\"b\">.S76 1982</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\" \" tag=\"082\">\n" +
            "<subfield code=\"a\">551.7</subfield>\n" +
            "<subfield code=\"2\">19</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "<subfield code=\"a\">Stokes, William Lee,</subfield>\n" +
            "<subfield code=\"d\">1915-1994.</subfield>\n" +
            "<subfield code=\"0\">(uri)http://id.loc.gov/authorities/names/n50011514</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"1\" ind2=\"0\" tag=\"245\">\n" +
            "<subfield code=\"a\">Essentials of earth history :</subfield>\n" +
            "<subfield code=\"b\">an introduction to historical geology /</subfield>\n" +
            "<subfield code=\"c\">W. Lee Stokes.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"250\">\n" +
            "<subfield code=\"a\">4th ed.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "<subfield code=\"a\">Englewood Cliffs, N.J. :</subfield>\n" +
            "<subfield code=\"b\">Prentice-Hall,</subfield>\n" +
            "<subfield code=\"c\">c1982.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "<subfield code=\"a\">xiv, 577 p. :</subfield>\n" +
            "<subfield code=\"b\">ill. ;</subfield>\n" +
            "<subfield code=\"c\">24 cm.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "<subfield code=\"a\">Includes bibliographies and index.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "<subfield code=\"a\">Historical geology.</subfield>\n" +
            "<subfield code=\"0\">\n" +
            "(uri)http://id.loc.gov/authorities/subjects/sh85061190\n" +
            "</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"998\">\n" +
            "<subfield code=\"a\">03/15/82</subfield>\n" +
            "<subfield code=\"s\">9110</subfield>\n" +
            "<subfield code=\"n\">NjP</subfield>\n" +
            "<subfield code=\"w\">DCLC818543B</subfield>\n" +
            "<subfield code=\"d\">03/15/82</subfield>\n" +
            "<subfield code=\"c\">ZG</subfield>\n" +
            "<subfield code=\"b\">WZ</subfield>\n" +
            "<subfield code=\"i\">820315</subfield>\n" +
            "<subfield code=\"l\">NJPG</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"948\">\n" +
            "<subfield code=\"a\">AACR2</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"911\">\n" +
            "<subfield code=\"a\">19921028</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"912\">\n" +
            "<subfield code=\"a\">19900820000000.0</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"959\">\n" +
            "<subfield code=\"a\">2000-06-13 00:00:00 -0500</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"852\">\n" +
            "<subfield code=\"0\">128532</subfield>\n" +
            "<subfield code=\"b\">rcppa</subfield>\n" +
            "<subfield code=\"h\">QE28.3 .S76 1982</subfield>\n" +
            "<subfield code=\"t\">1</subfield>\n" +
            "<subfield code=\"x\">tr fr sci</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"876\">\n" +
            "<subfield code=\"0\">128532</subfield>\n" +
            "<subfield code=\"a\">123431</subfield>\n" +
            "<subfield code=\"h\"/>\n" +
            "<subfield code=\"j\">Not Charged</subfield>\n" +
            "<subfield code=\"p\">32101068878931</subfield>\n" +
            "<subfield code=\"t\">1</subfield>\n" +
            "<subfield code=\"x\">Shared</subfield>\n" +
            "<subfield code=\"z\">PA</subfield>\n" +
            "</datafield>\n" +
            "</record>\n" +
            "</collection>";

    @Test
    public void submitCollectiontest() throws Exception{
        SubmitCollectionResponse submitCollectionResponse = new SubmitCollectionResponse();
        submitCollectionResponse.setItemBarcode("32101068878931");
        submitCollectionResponse.setMessage("ExceptionRecord");
        String inputRecords = updatedMarcXml;
        String institution = "PUL";
        boolean isCGDProtection = true;
        Map map = new HashMap();
        map.put(1,"PUL");
        Map<String,Object> requestParameters = new HashedMap();
        requestParameters.put(ScsbCommonConstants.INPUT_RECORDS,updatedMarcXml);
        requestParameters.put(ScsbCommonConstants.INSTITUTION,"PUL");
        requestParameters.put(ScsbCommonConstants.IS_CGD_PROTECTED,"true");
        List<Integer> reportRecordNumberList = new ArrayList<>();
        Set<Integer> processedBibIdSet = new HashSet<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();//Added to remove dummy record in solr
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();//Added to remove orphan record while unlinking
        Set<String> updatedBoundWithDummyRecordOwnInstBibIdSet = new HashSet<>();
        List<SubmitCollectionResponse> submitCollectionResponseList = new ArrayList<>();
        submitCollectionResponseList.add(submitCollectionResponse);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<Future> futures = new ArrayList<>();
        Mockito.when(setupDataService.getInstitutionCodeIdMap()).thenReturn(map);
        Mockito.when(submitCollectionBatchService.process(institution, inputRecords, processedBibIdSet, idMapToRemoveIndexList, bibIdMapToRemoveIndexList, "", reportRecordNumberList, true, isCGDProtection, updatedBoundWithDummyRecordOwnInstBibIdSet, exchange, executorService, futures)).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getValidationService()).thenReturn(validationService);
        Mockito.when(submitCollectionBatchService.getRepositoryService()).thenReturn(repositoryService);
        Mockito.when(repositoryService.getInstitutionDetailsRepository()).thenReturn(institutionDetailsRepository);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(Mockito.anyString())).thenReturn(getInstitutionEntity());
        Mockito.when(validationService.validateInstitution(Mockito.anyString())).thenReturn(true);
        Mockito.when(submitCollectionBatchService.processMarc(Mockito.anyString(),Mockito.anySet(),Mockito.anyMap(),Mockito.anyList(),Mockito.anyList(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.any(),Mockito.anySet(),Mockito.any(),Mockito.anyList())).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getMarcUtil()).thenReturn(marcUtil);
        Mockito.when(marcUtil.convertAndValidateXml(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyList())).thenCallRealMethod();
        Mockito.when(marcUtil.convertMarcXmlToRecord(Mockito.anyString())).thenCallRealMethod();
        ReflectionTestUtils.setField(marcUtil,"inputLimit",2);
        ReflectionTestUtils.setField(submitCollectionBatchService,"partitionSize",5000);
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(ScsbCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntityMultiVolume("456"));
        List<BibliographicEntity> updatedBibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntities("456");
        bibliographicEntity.setId(null);
        updatedBibliographicEntityList.add(bibliographicEntity);
        ReflectionTestUtils.setField(submitCollectionBatchService,"marcToBibEntityConverter",marcToBibEntityConverter);
        ReflectionTestUtils.setField(submitCollectionBatchService,"submitCollectionDAOService",submitCollectionDAOService);
        Mockito.when(marcToBibEntityConverter.convert(Mockito.any(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(Mockito.anyList(),Mockito.anyInt(),Mockito.anyMap(),Mockito.anySet(),Mockito.anyList(),Mockito.anyList(),Mockito.anySet(),Mockito.any(),Mockito.anyList())).thenReturn(updatedBibliographicEntityList);
        Mockito.when(submitCollectionBatchService.getConverter(Mockito.anyString())).thenCallRealMethod();
        ReflectionTestUtils.setField(submitCollectionBatchService,"marcToBibEntityConverter",marcToBibEntityConverter);
        Mockito.when(submitCollectionBatchService.getMarcToBibEntityConverter()).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getSubmitCollectionDAOService()).thenCallRealMethod();

        ResponseEntity response = sharedCollectionRestController.submitCollection(requestParameters);
        assertNotNull(response);
    }

    @Test
    public void submitCollectionException() throws Exception{
        SubmitCollectionResponse submitCollectionResponse = new SubmitCollectionResponse();
        submitCollectionResponse.setItemBarcode("32101068878931");
        submitCollectionResponse.setMessage("ExceptionRecord");
        String inputRecords = updatedMarcXml;
        String institution = "PUL";
        boolean isCGDProtection = true;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<Future> futures = new ArrayList<>();
        Map map = new HashMap();
        map.put(1,"PUL");
        Map<String,Object> requestParameters = new HashedMap();
        requestParameters.put(ScsbCommonConstants.INPUT_RECORDS,updatedMarcXml);
        requestParameters.put(ScsbCommonConstants.INSTITUTION,"PUL");
        requestParameters.put(ScsbCommonConstants.IS_CGD_PROTECTED,"true");
        List<Integer> reportRecordNumberList = new ArrayList<>();
        Set<Integer> processedBibIdSet = new HashSet<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();//Added to remove dummy record in solr
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();//Added to remove orphan record while unlinking
        Set<String> updatedBoundWithDummyRecordOwnInstBibIdSet = new HashSet<>();
        List<SubmitCollectionResponse> submitCollectionResponseList = new ArrayList<>();
        submitCollectionResponseList.add(submitCollectionResponse);
        Mockito.when(setupDataService.getInstitutionCodeIdMap()).thenReturn(map);
        Mockito.when(submitCollectionBatchService.process(institution, inputRecords, processedBibIdSet, idMapToRemoveIndexList, bibIdMapToRemoveIndexList, "", reportRecordNumberList, true, isCGDProtection, updatedBoundWithDummyRecordOwnInstBibIdSet, null, executorService, futures)).thenThrow(NullPointerException.class);
        ResponseEntity response = sharedCollectionRestController.submitCollection(requestParameters);
//        assertEquals(ScsbConstants.SUBMIT_COLLECTION_INTERNAL_ERROR,response.getBody());
    }

    @Test
    public void accessionBatch() throws Exception{
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        ResponseEntity response = sharedCollectionRestController.accessionBatch(accessionModelRequest);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void accession() throws Exception{
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(getAccessionRequests());
        ReflectionTestUtils.setField(sharedCollectionRestController,"inputLimit",10);
        ResponseEntity response = sharedCollectionRestController.accession(accessionModelRequest,exchange);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void accessionExceedLimit() throws Exception{
        List<AccessionRequest> accessionRequestList=new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        AccessionRequest accessionRequest1 = new AccessionRequest();
        accessionRequest1.setCustomerCode("PA");
        accessionRequest1.setItemBarcode("32101095533294");
        accessionRequestList.add(accessionRequest1);
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(accessionRequestList);
        ReflectionTestUtils.setField(sharedCollectionRestController,"inputLimit",0);
        ResponseEntity response = sharedCollectionRestController.accession(accessionModelRequest,exchange);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void ongoingAccessionJobNoPending() throws Exception{
        String response = sharedCollectionRestController.ongoingAccessionJob(exchange);
        assertEquals(ScsbCommonConstants.ACCESSION_NO_PENDING_REQUESTS,response);
    }

    @Test
    public void ongoingAccessionJobFailure() throws Exception{
        Mockito.when(bulkAccessionService.getAccessionRequest(Mockito.anyList())).thenReturn(getAccessionRequests());
        String response = sharedCollectionRestController.ongoingAccessionJob(exchange);
        assertEquals(ScsbCommonConstants.FAILURE,response);
    }

    @Test
    public void collectFuturesAndProcess(){
        List<Future> futures = new ArrayList<>();
        Set<Integer> bibIds = new HashSet<>();
        bibIds.add(1);
        Mockito.when(submitCollectionBatchService.indexData(any())).thenReturn("test");
        Mockito.when(commonUtil.collectFuturesAndUpdateMAQualifier(futures)).thenReturn(bibIds);
        ReflectionTestUtils.invokeMethod(sharedCollectionRestController,"collectFuturesAndProcess",futures);
    }
    private List<AccessionRequest> getAccessionRequests() {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        accessionRequestList.add(accessionRequest);
        return accessionRequestList;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }
    private BibliographicEntity getBibliographicEntityMultiVolume(String owningInstitutionBibId){
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1,owningInstitutionBibId);
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        ItemEntity itemEntity = getItemEntity("843617540");
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity,getItemEntity("78547557")));
        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);
        return bibliographicEntity;
    }
    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);
        return  holdingsEntity;
    }

    private ItemEntity getItemEntity(String OwningInstitutionItemId) {
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
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        return itemEntity;
    }

    private BibliographicEntity getBibliographicEntities(String owningInstitutionBibId){
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1,owningInstitutionBibId);
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        ItemEntity itemEntity = getItemEntity("843617540");
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));
        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);
        return bibliographicEntity;
    }

    private BibliographicEntity getBibliographicEntity(int bibliographicId,String owningInstitutionBibId) {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(bibliographicId);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setDeleted(false);
        return bibliographicEntity;
    }


}
