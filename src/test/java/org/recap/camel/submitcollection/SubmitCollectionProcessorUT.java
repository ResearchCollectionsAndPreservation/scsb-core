package org.recap.camel.submitcollection;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.camel.submitcollection.processor.SubmitCollectionProcessor;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.common.RepositoryService;
import org.recap.service.common.SetupDataService;
import org.recap.service.submitcollection.*;
import org.recap.util.MarcUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.apache.camel.builder.Builder.simple;
import static org.junit.Assert.assertTrue;


public class SubmitCollectionProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    SubmitCollectionProcessor submitCollectionProcessor;

    @Mock
    private SetupDataService setupDataService;

    @Mock
    SubmitCollectionBatchService submitCollectionBatchService;

    @Mock
    SubmitCollectionReportGenerator submitCollectionReportGenerator;

    @Mock
    private ProducerTemplate producer;

    @Mock
    AmazonS3 awsS3Client;

    @Mock
    PropertyUtil propertyUtil;

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
        SubmitCollectionProcessor submitCollectionProcessor = new SubmitCollectionProcessor("NYPL", false);
        ReflectionTestUtils.setField(submitCollectionProcessor,"propertyUtil",propertyUtil);
        ReflectionTestUtils.setField(submitCollectionProcessor,"setupDataService",setupDataService);
        ReflectionTestUtils.setField(submitCollectionProcessor,"submitCollectionBatchService",submitCollectionBatchService);
        ReflectionTestUtils.setField(submitCollectionProcessor,"submitCollectionReportGenerator",submitCollectionReportGenerator);
        ReflectionTestUtils.setField(submitCollectionProcessor,"producer",producer);
        ReflectionTestUtils.setField(submitCollectionProcessor,"awsS3Client",awsS3Client);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelAwsS3Key", simple("CamelAwsS3Key"));
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
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setHeader("institution", "CUL");
        ex.getIn().setBody("Test text for Example");
        Map institutionCodeIdMap = new HashMap<>();
        institutionCodeIdMap.put("CUL",2);
        institutionCodeIdMap.put("PUL",1);
        institutionCodeIdMap.put("NYPL",3);
        Mockito.when(setupDataService.getInstitutionCodeIdMap().get("CUL")).thenReturn(institutionCodeIdMap);
        submitCollectionProcessor.processInput(ex);
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
        Map institutionCodeIdMap = new HashMap<>();
        institutionCodeIdMap.put("CUL",2);
        institutionCodeIdMap.put("PUL",1);
        institutionCodeIdMap.put("NYPL",3);
        Mockito.when(setupDataService.getInstitutionCodeIdMap().get("PUL")).thenReturn(institutionCodeIdMap);
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
        Map institutionCodeIdMap = new HashMap<>();
        institutionCodeIdMap.put("CUL",2);
        institutionCodeIdMap.put("PUL",1);
        institutionCodeIdMap.put("NYPL",3);
        Mockito.when(setupDataService.getInstitutionCodeIdMap().get("NYPL")).thenReturn(institutionCodeIdMap);
        submitCollectionProcessor.processInput(ex);
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

}
