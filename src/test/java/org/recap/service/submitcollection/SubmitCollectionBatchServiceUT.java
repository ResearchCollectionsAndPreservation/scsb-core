package org.recap.service.submitcollection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.util.MarcUtil;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionBatchServiceUT {

    @InjectMocks
    SubmitCollectionBatchService submitCollectionBatchService;

    @Mock
    Record record;

    @Mock
    JAXBHandler jaxbHandler;

    @Mock
    Leader leader;

    @Mock
    private MarcUtil marcUtil;

    private String inputRecords = "\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n\" +\n" +
            "            \"<collection>\\n\" +\n" +
            "            \"   <record>\\n\" +\n" +
            "            \"      <leader>01302cas a2200361 a 4500</leader>\\n\" +\n" +
            "            \"      <controlfield tag=\\\"001\\\">202304</controlfield>\\n\" +\n" +
            "            \"      <controlfield tag=\\\"005\\\">20160526232735.0</controlfield>\\n\" +\n" +
            "            \"      <controlfield tag=\\\"008\\\">830323c19819999iluqx p   gv  0    0eng d</controlfield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"010\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">82640039</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"z\\\">81640039</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"z\\\">sn 81001329</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\" \\\" tag=\\\"022\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">0276-9948</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"035\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">(OCoLC)7466281</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"035\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">(CStRLIN)NJPG83-S372</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"035\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"9\\\">ABB7255TS-test</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"040\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">NSDP</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"d\\\">NjP</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"042\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">nsdp</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">lc</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"043\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">n-us-il</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\"0\\\" tag=\\\"050\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">K25</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">.N63</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\"0\\\" tag=\\\"222\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">University of Illinois law review</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\"0\\\" tag=\\\"245\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">University of Michigan.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"3\\\" ind2=\\\"0\\\" tag=\\\"246\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Law review</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"260\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Champaign, IL :</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">University of Illinois at Urbana-Champaign, College of Law,</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"c\\\">c1981-</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"300\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">v. ;</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"c\\\">27 cm.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"310\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">5 times a year,</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">2001-&amp;lt;2013&amp;gt;</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"321\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Quarterly,</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">1981-2000</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\" \\\" tag=\\\"362\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Vol. 1981, no. 1-</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"588\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Title from cover.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"588\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Latest issue consulted: Vol. 2013, no. 5.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\"0\\\" tag=\\\"650\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Law reviews</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"z\\\">Illinois.</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"0\\\">(uri)http://id.loc.gov/authorities/subjects/sh2009129243</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"2\\\" ind2=\\\" \\\" tag=\\\"710\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">University of Illinois at Urbana-Champaign.</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">College of Law.</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"0\\\">(uri)http://id.loc.gov/authorities/names/n50049213</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\"0\\\" tag=\\\"780\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"t\\\">University of Illinois law forum</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"x\\\">0041-963X</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"998\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">09/09/94</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"s\\\">9110</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"n\\\">NjP</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"w\\\">DCLC82640039S</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"d\\\">03/23/83</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"c\\\">DLJ</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">SZF</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"i\\\">940909</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"l\\\">NJPG</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"911\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">19940916</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"912\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">19970731060735.8</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"   </record>\\n\" +\n" +
            "            \"</collection>\";\n";
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
//        Mockito.when(submitCollectionBatchService.getMarcUtil()).thenReturn(marcUtil);
//        Mockito.when(marcUtil.convertMarcXmlToRecord(inputRecords)).thenReturn(recordList);
 ///       Mockito.when(marcUtil.convertAndValidateXml(inputRecords, checkLimit, recordList)).thenCallRealMethod();
        String result = submitCollectionBatchService.processMarc(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
            ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
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
        Mockito.when(marcUtil.convertAndValidateXml(inputRecords, checkLimit, recordList)).thenReturn("Maximum allowed input record");
        String result = submitCollectionBatchService.processMarc(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
                ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertNotNull(result);
    }
    @Test
    public void processSCSB() throws JAXBException {
        //String inputRecords = "test";
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        boolean checkLimit = true;
        boolean isCGDProtected = true;
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        BibRecords bibRecords = new BibRecords();
//        Mockito.when((BibRecords)jaxbHandler.getInstance().unmarshal(inputRecords, BibRecords.class)).thenReturn(bibRecords);
   //     submitCollectionBatchService.processSCSB(inputRecords,processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit,isCGDProtected,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
    }
    @Test
    public  void processSCSBException() throws JAXBException {
        //String inputRecords = "/home/jancy.roach/Workspace/Recap-4jdk11/Phase4-SCSB-Circ/src/test/resources";
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        processedBibIds.add(2);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        boolean checkLimit = true;
        boolean isCGDProtection = true;
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        BibRecords bibRecords = new BibRecords();
        //Mockito.when((BibRecords) jaxbHandler.getInstance().unmarshal(inputRecords, BibRecords.class)).thenReturn(bibRecords);
        String result = submitCollectionBatchService.processSCSB(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
                ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertNotNull(result);
    }

    private InstitutionEntity getInstitutionEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
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

}
