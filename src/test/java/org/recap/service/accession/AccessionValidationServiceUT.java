package org.recap.service.accession;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 3/6/17.
 */

public class AccessionValidationServiceUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(AccessionValidationServiceUT.class);

    @InjectMocks
    private AccessionValidationService accessionValidationService;

    @Mock
    private MarcUtil marcUtil;

    @Mock
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Mock
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Test
    public void validateBoundWithValidMarcRecordFromIls() throws URISyntaxException, IOException {
        File bibContentFile = getXmlContent("ValidBoundWithMarc.xml");
        String marcXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<Record> records = readMarcXml(marcXmlString);
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("32101075852200");
        accessionRequest.setCustomerCode("PA");
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(Mockito.anyString())).thenReturn(customerCodeEntity);
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithMarcRecordFromIls(records,accessionRequest);
        assertTrue(isValidBoundWithRecord);
    }

    @Test
    public void validateBoundWithInvalidMarcRecordFromIls() throws URISyntaxException, IOException {
        File bibContentFile = getXmlContent("InvalidBoundWithMarc.xml");
        String marcXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<Record> records = readMarcXml(marcXmlString);
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("32101075852200");
        accessionRequest.setCustomerCode("PA");
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        HoldingsEntity holdingsEntity=new HoldingsEntity();
        BibliographicEntity bibliographicEntity =   getBibliographicEntity("",marcXmlString);
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(holdingsEntity);
        Mockito.when(marcUtil.getDataFieldValue(records.get(0),"876","","","0")).thenReturn("1");
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithMarcRecordFromIls(records,accessionRequest);
        assertFalse(isValidBoundWithRecord);
    }

    @Test
    public void validateBoundWithValidScsbRecordFromIls() throws URISyntaxException, IOException, JAXBException {
        File bibContentFile = getXmlContent("ValidBoundWithSCSB.xml");
        String scsbXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<BibRecord> bibRecordList = getBibRecordList(scsbXmlString);
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(Mockito.anyString())).thenReturn(institutionEntity);
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithScsbRecordFromIls(bibRecordList);
        assertTrue(isValidBoundWithRecord);
    }

    @Test
    public void validateBoundWithInvalidScsbRecordFromIls() throws URISyntaxException, IOException, JAXBException {
        File bibContentFile = getXmlContent("InvalidBoundWithSCSB.xml");
        String scsbXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<BibRecord> bibRecordList = getBibRecordList(scsbXmlString);
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithScsbRecordFromIls(bibRecordList);
        assertFalse(isValidBoundWithRecord);
    }
    @Test
    public void validateValidHoldingRecord() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem("32456723441256","PA","24252","PUL","9919400","74534419");
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        StringBuilder errorMessage = new StringBuilder();
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(bibliographicEntity.getHoldingsEntities().get(0));
        boolean isValid = accessionValidationService.validateItemAndHolding(bibliographicEntity,false,false,errorMessage);
        assertTrue(isValid);
    }

    @Test
    public void validateInvalidHoldingRecordBoundwith() throws Exception {
        BibliographicEntity bibliographicEntity = saveMultiBibSingleHoldingsSingleItem("32456723441256","CU","24252","PUL","9919401","7453441");
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        StringBuilder errorMessage = new StringBuilder();
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(bibliographicEntity.getHoldingsEntities().get(0));
        boolean isValid = accessionValidationService.validateHolding(bibliographicEntity,false,false,errorMessage);
        assertTrue(isValid);
    }

    @Test
    public void validateInvalidHoldingRecordNonBoundwith() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibHoldingsItems("32456723441256","PA","24252","PUL","9919401","6224132","7453441");
        BibliographicEntity bibliographicEntity1 = saveBibHoldingsItems("32456723441256","PA","24252","PUL","99194011","6224132","7453441");
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101075852200");
        StringBuilder errorMessage = new StringBuilder();
        Mockito.when(holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(bibliographicEntity1.getHoldingsEntities().get(0));
        boolean isValid = accessionValidationService.validateHolding(bibliographicEntity,true,true,errorMessage);
        assertFalse(isValid);
    }

    @Test
    public void validateInvalidItemRecord() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem("32456723441256","PA","24252","PUL","991940","74534419");
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        StringBuilder errorMessage = new StringBuilder();
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(bibliographicEntity.getItemEntities().get(0));
        boolean isValid = accessionValidationService.validateItem(bibliographicEntity,false,false,errorMessage);
        assertFalse(isValid);
     }

    @Test
    public void validatevalidItemRecord() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem("32456723441256","PA","24252","PUL","9919400","7453441");
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = accessionValidationService.validateItem(bibliographicEntity,false,false,errorMessage);
        assertTrue(isValid);
    }

    private CustomerCodeEntity getCustomerCodeEntity() {
        CustomerCodeEntity customerCodeEntity=new CustomerCodeEntity();
        customerCodeEntity.setCustomerCode("PA");
        customerCodeEntity.setDescription("PRINCETON");
        customerCodeEntity.setOwningInstitutionId(1);
        return customerCodeEntity;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("CUL");
        institutionEntity.setInstitutionName("Columbia");
        institutionEntity.setId(2);
        return institutionEntity;
    }

    private List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
            logger.info(record.toString());
        }
        return recordList;
    }

    private File getXmlContent(String fileName) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource(fileName);
        return new File(resource.toURI());
    }

    private List<BibRecord> getBibRecordList(String scsbXmlString) throws JAXBException {
        BibRecords bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(scsbXmlString, BibRecords.class);
        return bibRecords.getBibRecordList();
    }

    public BibliographicEntity saveBibHoldingsItems( String itemBarcode, String customerCode, String callnumber, String institution, String owningInstBibId, String owningInstHoldingId, String owningInstItemId) throws Exception {
        File bibContentFile = getBibContentFile(institution);
        File holdingsContentFile = getHoldingsContentFile(institution);
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(owningInstBibId);
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(owningInstHoldingId));
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));

        ItemEntity itemEntity = getItemEntity(itemBarcode,customerCode,callnumber,owningInstItemId);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));

        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);
        return bibliographicEntity;

    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem(String itemBarcode, String customerCode, String callnumber, String institution,String owningInstBibId, String owningInstItemId) throws Exception {
        File bibContentFile = getBibContentFile(institution);
        File holdingsContentFile = getHoldingsContentFile(institution);
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(owningInstBibId, sourceBibContent);
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("9734816");
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));


        ItemEntity itemEntity = getItemEntity(itemBarcode,customerCode,callnumber,owningInstItemId);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));


        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;

    }

    public BibliographicEntity saveMultiBibSingleHoldingsSingleItem(String itemBarcode, String customerCode, String callnumber, String institution,String owningInstBibId, String owningInstItemId) throws Exception {
        File bibContentFile = getBibContentFile(institution);
        File holdingsContentFile = getHoldingsContentFile(institution);
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(owningInstBibId, sourceBibContent);
        BibliographicEntity bibliographicEntity1 = getBibliographicEntity(owningInstBibId, sourceBibContent);

        List<BibliographicEntity> bibliographicEntitylist = new ArrayList<>();
        bibliographicEntitylist.add(bibliographicEntity);
        bibliographicEntitylist.add(bibliographicEntity1);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("9734816");
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));


        ItemEntity itemEntity = getItemEntity(itemBarcode,customerCode,callnumber,owningInstItemId);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));


        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;

    }

    private BibliographicEntity getBibliographicEntity(String owningInstBibId, String sourceBibContent) {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(owningInstBibId);
        return bibliographicEntity;
    }

    public ItemEntity getItemEntity(String itemBarcode,String customerCode,String callnumber,String owningInstItemId){
        Random random = new Random();
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(owningInstItemId);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(itemBarcode);
        itemEntity.setCallNumber(callnumber);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode(customerCode);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        return itemEntity;
    }

    private File getBibContentFile(String institution) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile(String institution) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-HoldingsContent.xml");
        return new File(resource.toURI());
    }
}
