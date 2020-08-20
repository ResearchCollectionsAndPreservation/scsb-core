package org.recap.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import org.recap.ils.model.response.BibRecords;
import org.recap.model.jaxb.*;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.marc.BibMarcRecord;
import org.recap.model.marc.HoldingsMarcRecord;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;
import org.recap.util.DBReportUtil;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBException;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 23/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SCSBToBibEntityConverterUT{

    private final String scsbXmlContent1 = "<bibRecords>\n" +
            "    <bibRecord>\n" +
            "    <bib>\n" +
            "        <owningInstitutionId>NYPL</owningInstitutionId>\n" +
            "        <owningInstitutionBibId>.b100000125</owningInstitutionBibId>\n" +
            "        <content>\n" +
            "            <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "            <record>\n" +
            "                <controlfield tag=\"001\">NYPG001000005-B</controlfield>\n" +
            "                <controlfield tag=\"005\">20001116192418.8</controlfield>\n" +
            "                <controlfield tag=\"008\">841106s1970    le       b    000 0bara d</controlfield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "                    <subfield code=\"a\">NN</subfield>\n" +
            "                    <subfield code=\"c\">NN</subfield>\n" +
            "                    <subfield code=\"d\">WaOLN</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "                    <subfield code=\"a\">H&#xCC;&#xA3;&#xC4;w&#xC4;&#xAB;, &#xC4;&#xAA;l&#xC4;&#xAB;y&#xC4; Sal&#xC4;&#xAB;m.</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\"1\" ind2=\"3\" tag=\"245\">\n" +
            "                    <subfield code=\"a\">al-H&#xCC;&#xA3;ut&#xCC;&#xA3;ay&#xCA;&#xBC;ah :</subfield>\n" +
            "                    <subfield code=\"b\">f&#xC4;&#xAB; s&#xC4;&#xAB;ratihi wa-nafs&#xC4;&#xAB;yatihi wa-shi&#xCA;&#xBB;rihi /</subfield>\n" +
            "                    <subfield code=\"c\">bi-qalam &#xC4;&#xAA;l&#xC4;&#xAB;y&#xC4; H&#xCC;&#xA3;&#xC4;w&#xC4;&#xAB;.</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "                    <subfield code=\"a\">Bayr&#xC5;&#xAB;t :</subfield>\n" +
            "                    <subfield code=\"b\">D&#xC4;r al-Thaq&#xC4;fah,</subfield>\n" +
            "                    <subfield code=\"c\">1970.</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "                    <subfield code=\"a\">223 p. ;</subfield>\n" +
            "                    <subfield code=\"c\">25cm.</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "                    <subfield code=\"a\">Bibliography: p.221.</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"546\">\n" +
            "                    <subfield code=\"a\">In Arabic.</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\"1\" ind2=\"0\" tag=\"600\">\n" +
            "                    <subfield code=\"a\">H&#xCC;&#xA3;ut&#xCC;&#xA3;ay&#xCA;&#xBC;ah, Jarwal ibn Aws,</subfield>\n" +
            "                    <subfield code=\"d\">d. 650?</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"907\">\n" +
            "                    <subfield code=\"a\">.b100000125</subfield>\n" +
            "                    <subfield code=\"c\">m</subfield>\n" +
            "                    <subfield code=\"d\">a</subfield>\n" +
            "                    <subfield code=\"e\">-</subfield>\n" +
            "                    <subfield code=\"f\">ara</subfield>\n" +
            "                    <subfield code=\"g\">le </subfield>\n" +
            "                    <subfield code=\"h\">3</subfield>\n" +
            "                    <subfield code=\"i\">1</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"952\">\n" +
            "                    <subfield code=\"h\">*OFS 84-1997</subfield>\n" +
            "                </datafield>\n" +
            "                <leader>00777cam a2200229 i 4500</leader>\n" +
            "            </record>\n" +
            "        </collection>\n" +
            "    </content>\n" +
            "</bib>\n" +
            "<holdings>\n" +
            "    <holding>\n" +
            "        <owningInstitutionHoldingsId/>\n" +
            "        <content>\n" +
            "            <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "            <record>\n" +
            "                <datafield ind1=\"8\" ind2=\" \" tag=\"852\">\n" +
            "                    <subfield code=\"b\">rcma2</subfield>\n" +
            "                    <subfield code=\"h\">*OFS 84-1997</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"866\">\n" +
            "                    <subfield code=\"a\"/>\n" +
            "                </datafield>\n" +
            "            </record>\n" +
            "        </collection>\n" +
            "    </content>\n" +
            "    <items>\n" +
            "        <content>\n" +
            "            <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "            <record>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"876\">\n" +
            "                    <subfield code=\"p\">33433014514719</subfield>\n" +
            "                    <subfield code=\"h\">In Library Use</subfield>\n" +
            "                    <subfield code=\"a\">.i100000034</subfield>\n" +
            "                    <subfield code=\"j\">Available</subfield>\n" +
            "                    <subfield code=\"t\">1</subfield>\n" +
            "                </datafield>\n" +
            "                <datafield ind1=\" \" ind2=\" \" tag=\"900\">\n" +
            "                    <subfield code=\"a\">Shared</subfield>\n" +
            "                    <subfield code=\"b\">NA</subfield>\n" +
            "                </datafield>\n" +
            "            </record>\n" +
            "        </collection>\n" +
            "    </content>\n" +
            "</items>\n" +
            "</holding>\n" +
            "</holdings>\n" +
            "</bibRecord>\n" +
            "</bibRecords>\n";
    @InjectMocks
    private SCSBToBibEntityConverter scsbToBibEntityConverter;
    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;
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

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void convert() throws JAXBException {

        InstitutionEntity institutionEntity = getInstitutionEntity();

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
        Object scsbRecord = bibRecord;
        holdingsMarcRecord.setHoldingsRecord(record);
        bibMarcRecord.setBibRecord(record);
        bibMarcRecord.setHoldingsMarcRecords(Arrays.asList(holdingsMarcRecord));
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Owning Institution Bib Id cannot be null");
        Mockito.when(commonUtil.getInstitutionEntityMap()).thenReturn(new HashMap());
        Mockito.when(commonUtil.getCollectionGroupMap()).thenReturn(new HashMap());
        Mockito.when(marcUtil.buildBibMarcRecord(bibRecord)).thenReturn(bibMarcRecord);
        Mockito.when(marcUtil.convertMarcXmlToRecord(null)).thenReturn(Arrays.asList(record));
        Map result = scsbToBibEntityConverter.convert(scsbRecord,institutionEntity);
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
    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }
}
