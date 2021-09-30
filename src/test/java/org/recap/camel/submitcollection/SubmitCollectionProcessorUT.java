package org.recap.camel.submitcollection;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.*;
import org.recap.camel.submitcollection.processor.SubmitCollectionProcessor;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.service.BibliographicRepositoryDAO;
import org.recap.service.common.RepositoryService;
import org.recap.service.common.SetupDataService;
import org.recap.service.submitcollection.SubmitCollectionBatchService;
import org.recap.service.submitcollection.SubmitCollectionDAOService;
import org.recap.service.submitcollection.SubmitCollectionReportGenerator;
import org.recap.service.submitcollection.SubmitCollectionReportHelperService;
import org.recap.service.submitcollection.SubmitCollectionService;
import org.recap.service.submitcollection.SubmitCollectionValidationService;
import org.recap.util.CommonUtil;
import org.recap.util.MarcUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.Future;

import static org.apache.camel.builder.Builder.simple;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;


public class SubmitCollectionProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    SubmitCollectionProcessor submitCollectionProcessor;

    @Mock
    private SetupDataService setupDataService;

    @Mock
    SubmitCollectionBatchService submitCollectionBatchService;

    @Mock
    SubmitCollectionService submitCollectionService;

    @Mock
    CommonUtil commonUtil;

    @Mock
    SubmitCollectionReportGenerator submitCollectionReportGenerator;

    @Mock
    private ProducerTemplate producer;

    @Mock
    AmazonS3 awsS3Client;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Value("${" + PropertyKeyConstants.SCSB_BUCKET_NAME + "}")
    private String bucketName;

    @Mock
    SubmitCollectionValidationService validationService;

    @Mock
    RepositoryService repositoryService;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    InstitutionEntity institutionEntity;

    @Mock
    MarcUtil marcUtil;

    @Mock
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Mock
    Map responseMap;

    @Mock
    BibliographicEntity incomingBibliographicEntity;

    @Mock
    ItemEntity itemEntity;

    @Mock
    SubmitCollectionDAOService submitCollectionDAOService;

    @Mock
    SubmitCollectionValidationService submitCollectionValidationService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    SubmitCollectionReportHelperService submitCollectionReportHelperService;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    BibliographicRepositoryDAO bibliographicRepositoryDAO;

    @Mock
    EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(submitCollectionProcessor, "submitCollectionEmailSubject","Submit collection completed" );
        MockitoAnnotations.initMocks(this);
    }

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
    public void testSubmitCollectionProcessor() {
        SubmitCollectionProcessor submitCollectionProcessor = new SubmitCollectionProcessor("NYPL", false, "cgd_no_protection");
        ReflectionTestUtils.setField(submitCollectionProcessor,"propertyUtil",propertyUtil);
        ReflectionTestUtils.setField(submitCollectionProcessor,"setupDataService",setupDataService);
        ReflectionTestUtils.setField(submitCollectionProcessor,"submitCollectionBatchService",submitCollectionBatchService);
        ReflectionTestUtils.setField(submitCollectionProcessor,"submitCollectionReportGenerator",submitCollectionReportGenerator);
        ReflectionTestUtils.setField(submitCollectionProcessor,"producer",producer);
        ReflectionTestUtils.setField(submitCollectionProcessor,"awsS3Client",awsS3Client);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader(ScsbConstants.CAMEL_AWS_KEY, simple("CamelAwsS3Key/CamelAwsS3Key/CamelAwsS3Key"));
        ex.getIn().setHeader("CamelAwsS3BucketName", simple("CamelAwsS3BucketName"));
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setHeader("institution", "CUL");
        ex.getIn().setBody("Test text for Example");
        Exception e = new Exception();
        Throwable t = new ArithmeticException();
        e.addSuppressed(t);
        ex.setProperty("CamelExceptionCaught",e);
        Map institutionCodeIdMap=new HashMap();
        institutionCodeIdMap.put("NYPL",1);
        Mockito.when(setupDataService.getInstitutionCodeIdMap()).thenReturn(institutionCodeIdMap);
        Mockito.when(awsS3Client.doesObjectExist(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        Mockito.when(awsS3Client.doesBucketExistV2(Mockito.anyString())).thenReturn(true);

        try {
            submitCollectionProcessor.processInput(ex); } catch (Exception ef) {}
        try{
            submitCollectionProcessor.caughtException(ex);
        } catch (Exception ef) {}
        assertTrue(true);
    }

    @Test
    public void processInputForCUL(){
        ReflectionTestUtils.setField(submitCollectionProcessor, "institutionCode","CUL" );
        ReflectionTestUtils.setField(submitCollectionProcessor, "bucketName",bucketName );
        ReflectionTestUtils.setField(submitCollectionBatchService, "validationService",validationService);
        ReflectionTestUtils.setField(submitCollectionBatchService, "marcUtil",marcUtil);
        ReflectionTestUtils.setField(submitCollectionBatchService, "partitionSize",5000);
        ReflectionTestUtils.setField(submitCollectionBatchService, "marcToBibEntityConverter",marcToBibEntityConverter);
        ReflectionTestUtils.setField(submitCollectionBatchService, "submitCollectionDAOService",submitCollectionDAOService);
        ReflectionTestUtils.setField(submitCollectionDAOService, "submitCollectionValidationService",submitCollectionValidationService);
        ReflectionTestUtils.setField(submitCollectionDAOService, "repositoryService",repositoryService);
        ReflectionTestUtils.setField(submitCollectionDAOService, "submitCollectionReportHelperService",submitCollectionReportHelperService);
        ReflectionTestUtils.setField(submitCollectionDAOService, "bibliographicRepositoryDAO",bibliographicRepositoryDAO);
        ReflectionTestUtils.setField(submitCollectionDAOService, "entityManager",entityManager);
        ReflectionTestUtils.setField(marcUtil, "inputLimit",10);
        Mockito.when(bibliographicRepositoryDAO.saveOrUpdate(any())).thenReturn(incomingBibliographicEntity);
        Mockito.when(repositoryService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        StringBuilder errorMessage=new StringBuilder();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(responseMap.get("errorMessage")).thenReturn(errorMessage);
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Set<Integer> bibIds = new HashSet<>();
        bibIds.add(1);
        Mockito.when(itemDetailsRepository.findByBarcodeInAndOwningInstitutionId(Mockito.anyList(),Mockito.anyInt())).thenReturn(itemEntities);
        List<ItemEntity> fetchedItemBasedOnOwningInstitutionItemId=new ArrayList<>();
        Mockito.when(submitCollectionReportHelperService.getItemBasedOnOwningInstitutionItemIdAndOwningInstitutionId(Mockito.anyList())).thenReturn(fetchedItemBasedOnOwningInstitutionItemId);

        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(any())).thenReturn(false);
        Mockito.when(itemEntity.getBarcode()).thenReturn("123456");
        Mockito.when(itemEntity.getImsLocationEntity()).thenReturn(TestUtil.getImsLocationEntity(1,"RECAP","RECAP"));
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(1);
        List<BibliographicEntity> bibliographicEntityList=new ArrayList<>();
        bibliographicEntityList.add(incomingBibliographicEntity);
        Mockito.when(institutionEntity.getId()).thenReturn(2);
        Mockito.when(submitCollectionDAOService.updateDummyRecordForNonBoundWith(any(),Mockito.anyMap(),Mockito.anyList(),Mockito.anySet(), any(), any(),Mockito.anyList(), any(),Mockito.anyList())).thenCallRealMethod();
        Mockito.when(submitCollectionDAOService.getBarcodeSetFromItemEntityList(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(submitCollectionDAOService.getBarcodeItemEntityMap(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(Mockito.anyList(),Mockito.anyInt(),Mockito.anyMap(),Mockito.anySet(),Mockito.anyList(),Mockito.anySet(), any(),Mockito.anyList())).thenCallRealMethod();
        Mockito.when(submitCollectionDAOService.getBarcodeSetFromNonBoundWithBibliographicEntity(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(submitCollectionDAOService.getBarcodeItemEntityMapFromNonBoundWithBibliographicEntityList(Mockito.anyList())).thenCallRealMethod();
        Mockito.when(submitCollectionDAOService.getItemEntityListUsingBarcodeList(Mockito.anyList(),Mockito.anyInt())).thenCallRealMethod();
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntityList);
        Mockito.when(incomingBibliographicEntity.getItemEntities()).thenReturn(itemEntities);
        Mockito.when(incomingBibliographicEntity.getId()).thenReturn(1);
        Mockito.when(incomingBibliographicEntity.getCatalogingStatus()).thenReturn(ScsbCommonConstants.COMPLETE_STATUS);
        Mockito.when(itemEntity.getCatalogingStatus()).thenReturn(ScsbCommonConstants.COMPLETE_STATUS);
        Mockito.when(incomingBibliographicEntity.getOwningInstitutionBibId()).thenReturn("d34645");
        Mockito.when(responseMap.get("bibliographicEntity")).thenReturn(incomingBibliographicEntity);
        Mockito.when(marcToBibEntityConverter.convert(any(), any())).thenReturn(responseMap);
        Mockito.when(marcUtil.convertAndValidateXml(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyList())).thenCallRealMethod();
        Mockito.when(marcUtil.convertMarcXmlToRecord(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getMarcToBibEntityConverter()).thenCallRealMethod();
        Mockito.when(setupDataService.getInstitutionCodeIdMap()).thenReturn(getMap());
        Mockito.when(validationService.validateInstitution(Mockito.anyString())).thenReturn(true);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(Mockito.anyString())).thenReturn(institutionEntity);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(commonUtil.collectFuturesAndUpdateMAQualifier(any())).thenReturn(bibIds);
        Mockito.when(submitCollectionBatchService.processMarc(Mockito.anyString(),Mockito.anySet(),Mockito.anyMap(),Mockito.anyList(),Mockito.anyList(),Mockito.anyBoolean(),Mockito.anyBoolean(), any(),Mockito.anySet(), any(),Mockito.anyList())).thenCallRealMethod();
        Mockito.when(message.getBody(String.class)).thenReturn(updatedMarcXml);
        Mockito.when(message.getHeader(ScsbConstants.CAMEL_FILE_NAME_ONLY)).thenReturn("xmlFileName");
        Mockito.when(awsS3Client.doesObjectExist(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        Mockito.when(submitCollectionBatchService.getValidationService()).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getMarcUtil()).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getSubmitCollectionDAOService()).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getConverter(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(submitCollectionBatchService.getRepositoryService()).thenReturn(repositoryService);
        Mockito.when(repositoryService.getInstitutionDetailsRepository()).thenReturn(institutionDetailsRepository);
        Mockito.when(submitCollectionBatchService.process(Mockito.anyString(),Mockito.anyString(),Mockito.anySet(),Mockito.anyList(),Mockito.anyList(),Mockito.anyString(),Mockito.anyList(),Mockito.anyBoolean(),Mockito.anyBoolean(),Mockito.anySet(), any(), any(),Mockito.anyList())).thenCallRealMethod();
        submitCollectionProcessor.processInput(exchange);
    }


    @Test
    public void sendEmailForEmptyDirectory(){
        submitCollectionProcessor.sendEmailForEmptyDirectory();
        assertTrue(true);
    }
    @Test
    public void processInputForPUL(){
        ReflectionTestUtils.setField(submitCollectionProcessor, "institutionCode","PUL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "PUL");
        ex.getIn().setHeader("CamelFileParent", "PUL");
        ex.getIn().setHeader("institution", "PUL");
        ex.getIn().setBody("Test text for Example");
        Mockito.when(setupDataService.getInstitutionCodeIdMap()).thenReturn(getMap());
        submitCollectionProcessor.processInput(ex);
    }
    @Test
    public void processInputForNYPL(){
        ReflectionTestUtils.setField(submitCollectionProcessor, "institutionCode","NYPL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "NYPL");
        ex.getIn().setHeader("CamelFileParent", "NYPL");
        ex.getIn().setHeader("institution", "NYPL");
        ex.getIn().setBody("Test text for Example");
        Mockito.when(setupDataService.getInstitutionCodeIdMap()).thenReturn(getMap());
        submitCollectionProcessor.processInput(ex);
    }

    @Test
    public void performIndexingToRemoveBibs(){
        Map<String, String> idMap = new HashMap<>();
        idMap.put("1","1");
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        idMapToRemoveIndexList.add(idMap);
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        bibIdMapToRemoveIndexList.add(idMap);
        ReflectionTestUtils.invokeMethod(submitCollectionProcessor,"performIndexingToRemoveBibs",idMapToRemoveIndexList,bibIdMapToRemoveIndexList);
    }

    @Test
    public void performIndexingToRemoveBibsException(){
        Map<String, String> idMap = new HashMap<>();
        idMap.put("1","1");
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        idMapToRemoveIndexList.add(idMap);
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        bibIdMapToRemoveIndexList.add(idMap);
        Mockito.doThrow(new NullPointerException()).when(submitCollectionBatchService).removeBibFromSolrIndex(bibIdMapToRemoveIndexList);
        ReflectionTestUtils.invokeMethod(submitCollectionProcessor,"performIndexingToRemoveBibs",idMapToRemoveIndexList,bibIdMapToRemoveIndexList);
    }

    @Test
    public void performIndexingByOwningInstitutionBibIds(){
        Set<String> updatedBoundWithDummyRecordOwnInstBibIdSet = new HashSet<>();
        updatedBoundWithDummyRecordOwnInstBibIdSet.add("test");
        Integer institutionId = 1;
        Mockito.when(submitCollectionService.indexDataUsingOwningInstBibId(any(),any())).thenReturn("test");
        ReflectionTestUtils.invokeMethod(submitCollectionProcessor,"performIndexingByOwningInstitutionBibIds",updatedBoundWithDummyRecordOwnInstBibIdSet,institutionId);
    }

    @Test
    public void collectFuturesAndProcess(){
        List<Future> futures = new ArrayList<>();
        Set<Integer> bibIds = new HashSet<>();
        bibIds.add(1);
        ReflectionTestUtils.setField(submitCollectionProcessor,"solrMaxDocSizeToUsePartialIndex",3);
        Mockito.when(submitCollectionBatchService.indexData(any())).thenReturn("test");
        Mockito.when(commonUtil.collectFuturesAndUpdateMAQualifier(futures)).thenReturn(bibIds);
        ReflectionTestUtils.invokeMethod(submitCollectionProcessor,"collectFuturesAndProcess",futures);
    }

    @Test
    public void getReportDataRequest(){
        String xmlFileName = "test";
        ReflectionTestUtils.setField(submitCollectionProcessor,"institutionCode","PUL");
        ReflectionTestUtils.invokeMethod(submitCollectionProcessor,"getReportDataRequest",xmlFileName);
    }

    @Test
    public void getEmailPayLoad(){
        String xmlFileName = "test";
        String reportFileName = "test";
        ReflectionTestUtils.setField(submitCollectionProcessor,"submitCollectionEmailSubject","test@gmail.com");
        ReflectionTestUtils.setField(submitCollectionProcessor,"institutionCode","PUL");
        ReflectionTestUtils.setField(submitCollectionProcessor,"submitCollectionReportS3Dir","test");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey("PUL", PropertyKeyConstants.ILS.ILS_EMAIL_SUBMIT_COLLECTION_TO)).thenReturn("test@gmail.com");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey("PUL", PropertyKeyConstants.ILS.ILS_EMAIL_SUBMIT_COLLECTION_CC)).thenReturn("test@gmail.com");
        ReflectionTestUtils.invokeMethod(submitCollectionProcessor,"getEmailPayLoad",xmlFileName,reportFileName);
    }

    private Map getMap() {
        Map institutionCodeIdMap = new HashMap<>();
        institutionCodeIdMap.put("CUL", 2);
        institutionCodeIdMap.put("PUL", 1);
        institutionCodeIdMap.put("NYPL", 3);
        return institutionCodeIdMap;
    }

}
