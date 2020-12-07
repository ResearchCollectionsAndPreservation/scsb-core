
package org.recap.service.submitcollection;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.converter.SCSBToBibEntityConverter;
import org.recap.model.jaxb.Bib;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.util.CommonUtil;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class SubmitCollectionBatchServiceUT extends BaseTestCaseUT {

    @InjectMocks
    SubmitCollectionBatchService submitCollectionBatchService;

    @Mock
    Record record;

    @Mock
    JAXBHandler jaxbHandler;

    @Mock
    Leader leader;

    @Mock
    MarcUtil marcUtil;

    @Mock
    CommonUtil commonUtil;

    @Mock
    SCSBToBibEntityConverter scsbToBibEntityConverter;

    @Mock
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Mock
    SubmitCollectionReportHelperService submitCollectionReportHelperService;

    @Mock
    SubmitCollectionDAOService submitCollectionDAOService;

    @Value("${submit.collection.input.limit}")
    Integer inputLimit;

    @Value("${submit.collection.partition.size}")
    Integer partitionSize;


    private String inputRecords =  "<collection xmlns=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +
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
    public void processMarc(){
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        processedBibIds.add(2);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("1",submitCollectionReportInfos);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("1","1");
        idMapToRemoveIndexList.add(stringMap);
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        bibIdMapToRemoveIndexList.add(stringMap);
        boolean checkLimit = true;
        boolean isCGDProtection = true;
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        updatedDummyRecordOwnInstBibIdSet.add("123456");
        InstitutionEntity institutionEntity = getInstitutionEntity();
        record.setId(1l);
        leader.setId(1l);
        leader.setBaseAddressOfData(1);
        record.setLeader(leader);
        record.setType("Submit");
        List<Record> recordList = new ArrayList<>();
        recordList.add(record);
        ReflectionTestUtils.setField(marcUtil,"inputLimit",2);
        ReflectionTestUtils.setField(submitCollectionBatchService,"partitionSize",partitionSize);
        Mockito.when(marcUtil.convertAndValidateXml(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyList())).thenCallRealMethod();
        Mockito.when(marcUtil.convertMarcXmlToRecord(Mockito.anyString())).thenCallRealMethod();
        Map responseMap=new HashMap();
        StringBuilder stringBuilder=new StringBuilder();
        responseMap.put("errorMessage",stringBuilder);
        responseMap.put(RecapCommonConstants.BIBLIOGRAPHICENTITY,getBibliographicEntityMultiVolume("456"));
        List<BibliographicEntity> updatedBibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntities("456");
        bibliographicEntity.setBibliographicId(null);
        updatedBibliographicEntityList.add(bibliographicEntity);
        Mockito.when(marcToBibEntityConverter.convert(Mockito.any(),Mockito.any())).thenReturn(responseMap);
        Mockito.when(submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(Mockito.anyList(),Mockito.anyInt(),Mockito.anyMap(),Mockito.anySet(),Mockito.anyList(),Mockito.anyList(),Mockito.anySet())).thenReturn(updatedBibliographicEntityList);
        String result = submitCollectionBatchService.processMarc(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertNull(result);
    }

    @Test
    public void processMarcWithInvalidMessage(){
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        processedBibIds.add(2);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        boolean checkLimit = true;
        boolean isCGDProtection = true;
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        List<Record> recordList = new ArrayList<>();
        Mockito.when(marcUtil.convertAndValidateXml(inputRecords, checkLimit, recordList)).thenReturn(RecapConstants.SUBMIT_COLLECTION_LIMIT_EXCEED_MESSAGE);
        String result = submitCollectionBatchService.processMarc(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertEquals(RecapConstants.SUBMIT_COLLECTION_LIMIT_EXCEED_MESSAGE,result);
    }

    @Test
    public void processSCSBExceedLimit() throws JAXBException {
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        boolean checkLimit = true;
        boolean isCGDProtected = true;
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        BibRecords bibRecords = new BibRecords();
        List<BibRecord> bibRecordList=new ArrayList<>();
        BibRecord bibRecord=new BibRecord();
        bibRecord.setBib(new Bib());
        bibRecordList.add(bibRecord);
        bibRecords.setBibRecordList(bibRecordList);
        ReflectionTestUtils.setField(submitCollectionBatchService,"inputLimit",0);
        Mockito.when(commonUtil.extractBibRecords(Mockito.anyString())).thenReturn(bibRecords);
        String result =  submitCollectionBatchService.processSCSB(inputRecords,processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit,isCGDProtected,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertEquals(RecapConstants.SUBMIT_COLLECTION_LIMIT_EXCEED_MESSAGE+ " " + 0,result);

    }


    @Test
    public void processSCSBNonBoundwith() throws JAXBException {
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        BibRecords bibRecords = new BibRecords();
        List<BibRecord> bibRecordList=new ArrayList<>();
        BibRecord bibRecord=new BibRecord();
        bibRecord.setBib(new Bib());
        bibRecordList.add(bibRecord);
        bibRecords.setBibRecordList(bibRecordList);
        ReflectionTestUtils.setField(submitCollectionBatchService,"inputLimit",1);
        ReflectionTestUtils.setField(submitCollectionBatchService,"partitionSize",partitionSize);
        Mockito.when(commonUtil.extractBibRecords(Mockito.anyString())).thenReturn(bibRecords);
        Map responseMap=new HashMap();
        StringBuilder errorMessage = new StringBuilder();
        responseMap.put("errorMessage",errorMessage);
        responseMap.put("bibliographicEntity",getBibliographicEntities("456"));
        Mockito.when(scsbToBibEntityConverter.convert(Mockito.any(),Mockito.any())).thenReturn(responseMap);
        List<BibliographicEntity> updatedBibliographicEntityList = new ArrayList<>();
        updatedBibliographicEntityList.add(getBibliographicEntities("456"));
        Mockito.when(submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(Mockito.anyList(),Mockito.anyInt(),Mockito.anyMap(),Mockito.anySet(),Mockito.anyList(),Mockito.anySet())).thenReturn(updatedBibliographicEntityList);
        String result =  submitCollectionBatchService.processSCSB(inputRecords,processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,true,true,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertEquals(null,result);
    }

    @Test
    public void processSCSBBoundwith() throws JAXBException {
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        BibRecords bibRecords = new BibRecords();
        List<BibRecord> bibRecordList=new ArrayList<>();
        BibRecord bibRecord=new BibRecord();
        bibRecord.setBib(new Bib());
        bibRecordList.add(bibRecord);
        bibRecords.setBibRecordList(bibRecordList);
        ReflectionTestUtils.setField(submitCollectionBatchService,"inputLimit",1);
        ReflectionTestUtils.setField(submitCollectionBatchService,"partitionSize",partitionSize);
        Mockito.when(commonUtil.extractBibRecords(Mockito.anyString())).thenReturn(bibRecords);
        Map responseMap=new HashMap();
        StringBuilder errorMessage = new StringBuilder();
        responseMap.put("errorMessage",errorMessage);
        responseMap.put("bibliographicEntity",getBibliographicEntityBoundwith());
        Mockito.when(scsbToBibEntityConverter.convert(Mockito.any(),Mockito.any())).thenReturn(responseMap);
        List<BibliographicEntity> updatedBibliographicEntityList = new ArrayList<>();
        updatedBibliographicEntityList.add(getBibliographicEntityBoundwith());
        Mockito.when(submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(Mockito.anyList(),Mockito.anyInt(),Mockito.anyMap(),Mockito.anySet(),Mockito.anyList(),Mockito.anyList(),Mockito.anySet())).thenReturn(updatedBibliographicEntityList);
        String result =  submitCollectionBatchService.processSCSB(inputRecords,processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,true,true,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertEquals(null,result);

    }

    @Ignore
    public void processSCSBInvalid() throws JAXBException {
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        BibRecords bibRecords = new BibRecords();
        List<BibRecord> bibRecordList=new ArrayList<>();
        BibRecord bibRecord=new BibRecord();
        bibRecord.setBib(new Bib());
        bibRecordList.add(bibRecord);
        bibRecords.setBibRecordList(bibRecordList);
        ReflectionTestUtils.setField(submitCollectionBatchService,"inputLimit",1);
        ReflectionTestUtils.setField(submitCollectionBatchService,"partitionSize",partitionSize);
        Mockito.when(commonUtil.extractBibRecords(Mockito.anyString())).thenReturn(bibRecords);
        Map responseMap=new HashMap();
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(1);
        responseMap.put("errorMessage",errorMessage);
        responseMap.put("bibliographicEntity",getBibliographicEntityBoundwith());
        Mockito.when(scsbToBibEntityConverter.convert(Mockito.any(),Mockito.any())).thenReturn(responseMap);
        List<BibliographicEntity> updatedBibliographicEntityList = new ArrayList<>();
        updatedBibliographicEntityList.add(getBibliographicEntityBoundwith());
        Mockito.when(submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(Mockito.anyList(),Mockito.anyInt(),Mockito.anyMap(),Mockito.anySet(),Mockito.anyList(),Mockito.anyList(),Mockito.anySet())).thenReturn(updatedBibliographicEntityList);
        String result =  submitCollectionBatchService.processSCSB(inputRecords,processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,true,true,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertEquals(null,result);

    }

    @Test
    public void processSCSBInvalid2() throws JAXBException {
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        BibRecords bibRecords = new BibRecords();
        List<BibRecord> bibRecordList=new ArrayList<>();
        BibRecord bibRecord=new BibRecord();
        bibRecord.setBib(new Bib());
        bibRecordList.add(bibRecord);
        bibRecords.setBibRecordList(bibRecordList);
        ReflectionTestUtils.setField(submitCollectionBatchService,"inputLimit",1);
        ReflectionTestUtils.setField(submitCollectionBatchService,"partitionSize",partitionSize);
        Mockito.when(commonUtil.extractBibRecords(Mockito.anyString())).thenReturn(bibRecords);
        Map responseMap=new HashMap();
        responseMap.put("errorMessage",null);
        responseMap.put("bibliographicEntity",getBibliographicEntityBoundwith());
        Mockito.when(scsbToBibEntityConverter.convert(Mockito.any(),Mockito.any())).thenReturn(responseMap);
        List<BibliographicEntity> updatedBibliographicEntityList = new ArrayList<>();
        updatedBibliographicEntityList.add(getBibliographicEntityBoundwith());
        Mockito.when(submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(Mockito.anyList(),Mockito.anyInt(),Mockito.anyMap(),Mockito.anySet(),Mockito.anyList(),Mockito.anyList(),Mockito.anySet())).thenReturn(updatedBibliographicEntityList);
        String result =  submitCollectionBatchService.processSCSB(inputRecords,processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,true,true,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertEquals(null,result);

    }


    @Test
    public  void processSCSBException() throws JAXBException {
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        Mockito.when(commonUtil.extractBibRecords(Mockito.anyString())).thenThrow(JAXBException.class);
        String result = submitCollectionBatchService.processSCSB(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,true,true,getInstitutionEntity(),updatedDummyRecordOwnInstBibIdSet);
        assertEquals(RecapConstants.INVALID_SCSB_XML_FORMAT_MESSAGE,result);
    }




    private InstitutionEntity getInstitutionEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }

    private BibliographicEntity getBibliographicEntity(int bibliographicId,String owningInstitutionBibId) {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(bibliographicId);
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

    private BibliographicEntity getBibliographicEntityBoundwith(){
        BibliographicEntity bibliographicEntity = getBibliographicEntity(123456,"34558");
        bibliographicEntity.setCatalogingStatus("inComplete");
        HoldingsEntity holdingsEntity =getHoldingsEntity();
        ItemEntity itemEntity =getItemEntity("843617540");
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(getBibliographicEntities("45568"));
        bibliographicEntities.add(getBibliographicEntities("456"));
        itemEntity.setBibliographicEntities(bibliographicEntities);
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

}

