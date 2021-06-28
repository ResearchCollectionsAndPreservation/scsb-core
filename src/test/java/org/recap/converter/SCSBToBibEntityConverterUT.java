package org.recap.converter;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jaxb.Bib;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jpa.*;
import org.recap.model.marc.BibMarcRecord;
import org.recap.model.marc.HoldingsMarcRecord;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;
import org.recap.util.DBReportUtil;
import org.recap.util.MarcUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 23/12/16.
 */

public class SCSBToBibEntityConverterUT extends BaseTestCaseUT {

    @InjectMocks
    SCSBToBibEntityConverter scsbToBibEntityConverter;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    CommonUtil commonUtil;

    @Mock
    DBReportUtil dbReportUtil;

    @Mock
    MarcUtil marcUtil;

    @Mock
    Record record;

    @Mock
    BibMarcRecord bibMarcRecord;

    @Mock
    HoldingsMarcRecord holdingsMarcRecord;

    @Mock
    CollectionType collectionType;

    @Mock
    InstitutionEntity institutionEntity;

    @Mock
    BibRecord bibRecord;

    @Mock
    Bib bib;

    @Mock
    Record bibRecordObject;

    @Mock
    ReportEntity bibReportEntity;

    @Mock
    HoldingsEntity holdingsEntity;

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }

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
    public void convert() throws Exception {
        BibRecord bibRecord = new BibRecord();
        Bib bib = new Bib();
        Holdings holdings = new Holdings();
        Holding holding = new Holding();
        holding.setOwningInstitutionHoldingsId("1");
        ContentType contentType = new ContentType();
        contentType.setCollection(collectionType);
        holding.setContent(contentType);
        holdings.setHolding(Arrays.asList(holding));
        bibRecord.setBib(bib);
        bibRecord.setHoldings(Arrays.asList(holdings));
        holdingsMarcRecord.setHoldingsRecord(record);
        bibMarcRecord.setBibRecord(record);
        bibMarcRecord.setHoldingsMarcRecords(Arrays.asList(holdingsMarcRecord));
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Owning Institution Bib Id cannot be null");

        Mockito.when(commonUtil.getInstitutionEntityMap()).thenReturn(new HashMap());
        Mockito.when(commonUtil.getCollectionGroupMap()).thenReturn(new HashMap());
        Mockito.when(marcUtil.buildBibMarcRecord(Mockito.any(BibRecord.class))).thenReturn(bibMarcRecord);
        Mockito.when(marcUtil.convertMarcXmlToRecord(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(commonUtil.buildHoldingsEntity(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.anyString(),Mockito.anyString())).thenReturn(getHoldingsEntity());
        Map<String, Object> map = new HashMap<>();
        BibliographicEntity bibliographicEntity=saveBibSingleHoldingsSingleItem("33433002031718","NA","NYPL",".b100000186");
        map.put(ScsbConstants.BIBLIOGRAPHIC_ENTITY,bibliographicEntity);
        Mockito.when(marcUtil.extractXmlAndSetEntityToMap(Mockito.any(),Mockito.any(),Mockito.anyMap(),Mockito.any())).thenReturn(map);
        Map result = scsbToBibEntityConverter.convert(getBibRecords().getBibRecordList().get(0),getInstitutionEntity());
        assertNotNull(result);
    }

    @Test
    public void convertItemValidation() throws Exception {
        BibRecord bibRecord = new BibRecord();
        Bib bib = new Bib();
        Holdings holdings = new Holdings();
        Holding holding = new Holding();
        holding.setOwningInstitutionHoldingsId("1");
        ContentType contentType = new ContentType();
        contentType.setCollection(collectionType);
        holding.setContent(contentType);
        holdings.setHolding(Arrays.asList(holding));
        bibRecord.setBib(bib);
        bibRecord.setHoldings(Arrays.asList(holdings));
        holdingsMarcRecord.setHoldingsRecord(record);
        bibMarcRecord.setBibRecord(record);
        bibMarcRecord.setHoldingsMarcRecords(Arrays.asList(holdingsMarcRecord));
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Owning Institution Bib Id cannot be null");
        BibRecord bibRecordx = (BibRecord) getBibRecords().getBibRecordList().get(0);
        CollectionType itemContentCollectionx=bibRecordx.getHoldings().get(0).getHolding().get(0).getItems().get(0).getContent().getCollection();
        String itemContentx = itemContentCollectionx.serialize(itemContentCollectionx);
        MarcUtil marcUtil1=new MarcUtil();
        List<Record> itemRecordListx = marcUtil1.convertMarcXmlToRecord(itemContentx);
        Record itemRecordx=itemRecordListx.get(0);
        Mockito.when(marcUtil.getDataFieldValue(itemRecordx,"876", 'p')).thenReturn("33433002031718");
        Mockito.when(marcUtil.getDataFieldValue(itemRecordx,"900", 'b')).thenReturn("NA");
        Mockito.when(marcUtil.getDataFieldValue(itemRecordx,"876", 'k')).thenReturn("itemLibrary");
        Mockito.when(marcUtil.getDataFieldValue(itemRecordx,"876", 't')).thenReturn("1");
        Mockito.when(marcUtil.getDataFieldValue(itemRecordx,"900", 'a')).thenReturn("Shared");
        Mockito.when(marcUtil.getDataFieldValue(itemRecordx,"876", 'h')).thenReturn("In Library Use");
        Mockito.when(marcUtil.getDataFieldValue(itemRecordx,"876", 'a')).thenReturn(".i100000046");
        Mockito.when(commonUtil.getInstitutionEntityMap()).thenReturn(new HashMap());
        Map<String, Integer> collectionGroupMap=new HashMap<>();
        collectionGroupMap.put("Shared",1);
        Mockito.when(commonUtil.getCollectionGroupMap()).thenReturn(collectionGroupMap);
        Mockito.when(marcUtil.buildBibMarcRecord(Mockito.any(BibRecord.class))).thenReturn(bibMarcRecord);
        Mockito.when(marcUtil.convertMarcXmlToRecord(Mockito.anyString())).thenReturn(itemRecordListx);
        Mockito.when(commonUtil.buildHoldingsEntity(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.anyString(),Mockito.anyString())).thenReturn(getHoldingsEntity());
        Map<String, Object> map = new HashMap<>();
        BibliographicEntity bibliographicEntity=saveBibSingleHoldingsSingleItem("33433002031718","NA","NYPL",".b100000186");
        map.put(ScsbConstants.BIBLIOGRAPHIC_ENTITY,bibliographicEntity);
        Mockito.when(marcUtil.extractXmlAndSetEntityToMap(Mockito.any(),Mockito.any(),Mockito.anyMap(),Mockito.any())).thenReturn(map);
        Map result = scsbToBibEntityConverter.convert(getBibRecords().getBibRecordList().get(0),getInstitutionEntity());
        assertNotNull(result);
    }

    @Test
    public void convertHoldingValidation() throws Exception {
        Mockito.when(bibRecord.getBib()).thenReturn(bib);
        Mockito.when(bib.getOwningInstitutionBibId()).thenReturn("");
        Mockito.when(marcUtil.buildBibMarcRecord(bibRecord)).thenReturn(bibMarcRecord);
        Mockito.when(bibMarcRecord.getBibRecord()).thenReturn(bibRecordObject);
        Map<String, Object> bibMap=new HashMap<>();
        bibMap.put("bibReportEntity",bibReportEntity);
        Mockito.when(marcUtil.extractXmlAndSetEntityToMap(Mockito.any(),Mockito.any(),Mockito.anyMap(),Mockito.any())).thenReturn(bibMap);
        Mockito.when(commonUtil.buildHoldingsEntity(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.anyString(),Mockito.anyString())).thenReturn(holdingsEntity);
        Mockito.when(marcUtil.convertMarcXmlToRecord(Mockito.anyString())).thenCallRealMethod();
        Map result = scsbToBibEntityConverter.convert(bibRecord,institutionEntity);
        assertNotNull(result);
    }

    @Test
    public void convertException() throws JAXBException {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        BibRecord bibRecord = new BibRecord();
        Bib bib = new Bib();
        bibRecord.setBib(bib);
        Object scsbRecord = bibRecord;
        Mockito.when(commonUtil.getInstitutionEntityMap()).thenReturn(new HashMap());
        Mockito.when(commonUtil.getCollectionGroupMap()).thenReturn(new HashMap());
        Map result = scsbToBibEntityConverter.convert(scsbRecord,institutionEntity);
        assertNotNull(result);
    }

    private BibRecords getBibRecords() throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(BibRecords.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        InputStream stream = new ByteArrayInputStream(scsbXmlContent.getBytes(StandardCharsets.UTF_8));
        XMLStreamReader xsr = xif.createXMLStreamReader(stream);
        Unmarshaller um = context.createUnmarshaller();
        BibRecords   bibRecords = (BibRecords) JAXBIntrospector.getValue(um.unmarshal(xsr));
        return bibRecords;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }
    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setDeleted(false);
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy(ScsbCommonConstants.ACCESSION);
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy(ScsbCommonConstants.ACCESSION);
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId("5123222f-2333-413e-8c9c-cb8709f010c3");
        return holdingsEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem(String itemBarcode, String customerCode, String institution,String owningInstBibId) throws Exception {

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("sourceBibContent".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(3);
        bibliographicEntity.setOwningInstitutionBibId(owningInstBibId);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setDeleted(false);
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy(ScsbCommonConstants.ACCESSION);
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy(ScsbCommonConstants.ACCESSION);
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId("5123222f-2333-413e-8c9c-cb8709f010c3");

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(".i100000046");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(itemBarcode);
        itemEntity.setCallNumber("1");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode(customerCode);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        return bibliographicEntity;

    }
}
