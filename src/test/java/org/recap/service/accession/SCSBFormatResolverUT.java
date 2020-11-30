package org.recap.service.accession;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.Items;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.AccessionUtil;
import org.recap.util.CommonUtil;
import org.recap.util.MarcUtil;
import org.recap.util.PropertyUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SCSBFormatResolverUT extends BaseTestCaseUT{

    @InjectMocks
    SCSBFormatResolver mockSCSBFormatResolver;

    @Mock
    BibRecords bibRecords;

    @Mock
    BibRecord bibRecord;

    @Mock
    AccessionValidationService accessionValidationService;

    @Mock
    CommonUtil commonUtil;

    @Mock
    AccessionUtil accessionUtil;

    @Mock
    Holdings holdingsRecord;

    @Mock
    Holding holding;

    @Mock
    Items item;

    @Mock
    ContentType itemContent;

    @Mock
    CollectionType itemContentCollection;

    @Mock
    RecordType recordType;

    @Mock
    MarcUtil marcUtil;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    BibDataFactory bibDataFactory;

    @Mock
    BibDataForAccessionInterface bibDataForAccessionInterface;

    @Test
    public void processXmlBoundwith() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        List<BibRecord> bibRecordList=new ArrayList<>();
        bibRecordList.add(bibRecord);
        bibRecordList.add(bibRecord);
        Mockito.when(bibRecords.getBibRecordList()).thenReturn(bibRecordList);
        Mockito.when(accessionValidationService.validateBoundWithScsbRecordFromIls(Mockito.anyList())).thenReturn(true);
        Mockito.when(commonUtil.getUpdatedDataResponse(Mockito.anySet(),Mockito.anyList(),Mockito.anyString(),Mockito.anyList(),Mockito.any(),Mockito.anyBoolean(),Mockito.anyInt(),Mockito.any())).thenReturn(RecapCommonConstants.SUCCESS);
        String response=mockSCSBFormatResolver.processXml(getAccessionResponses(),bibRecords,responseMapList,"NYPL",reportDataEntityList,accessionRequest);
        assertEquals(RecapCommonConstants.SUCCESS,response);
    }

    @Test
    public void processXml() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        List<BibRecord> bibRecordList=new ArrayList<>();
        bibRecordList.add(bibRecord);
        bibRecordList.add(bibRecord);
        Mockito.when(bibRecords.getBibRecordList()).thenReturn(bibRecordList);
        Mockito.when(accessionValidationService.validateBoundWithScsbRecordFromIls(Mockito.anyList())).thenReturn(false);
        String response=mockSCSBFormatResolver.processXml(getAccessionResponses(),bibRecords,responseMapList,"NYPL",reportDataEntityList,accessionRequest);
        assertEquals(RecapConstants.INVALID_BOUNDWITH_RECORD,response);
    }

    @Test
    public void getItemEntityFromRecord() throws Exception {
        List<BibRecord> bibRecordList=new ArrayList<>();
        bibRecordList.add(bibRecord);
        Mockito.when(bibRecords.getBibRecordList()).thenReturn(bibRecordList);
        List<Holdings> holdings=new ArrayList<>();
        holdings.add(holdingsRecord);
        Mockito.when(bibRecord.getHoldings()).thenReturn(holdings);
        List<Holding> holdingList=new ArrayList<>();
        holdingList.add(holding);
        Mockito.when(holdingsRecord.getHolding()).thenReturn(holdingList);
        List<Items> items=new ArrayList<>();
        items.add(item);
        Mockito.when(holding.getItems()).thenReturn(items);
        Mockito.when(item.getContent()).thenReturn(itemContent);
        Mockito.when(itemContent.getCollection()).thenReturn(itemContentCollection);
        List<RecordType> itemRecordTypes=new ArrayList<>();
        itemRecordTypes.add(recordType);
        Mockito.when(itemContentCollection.getRecord()).thenReturn(itemRecordTypes);
        Mockito.when(marcUtil.getDataFieldValueForRecordType(recordType,"876", null, null, "a")).thenReturn("1");
        ItemEntity itemEntity1=new ItemEntity();
        itemEntity1.setBarcode("123456");
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(itemEntity1);
        ItemEntity itemEntity=mockSCSBFormatResolver.getItemEntityFromRecord(bibRecords,3);
        assertEquals("123456",itemEntity.getBarcode());
    }

    @Test
    public void getOwningInstitutionItemIdFromBibRecord() throws Exception {
        List<BibRecord> bibRecordList=new ArrayList<>();
        bibRecordList.add(bibRecord);
        Mockito.when(bibRecords.getBibRecordList()).thenReturn(bibRecordList);
        List<Holdings> holdings=new ArrayList<>();
        holdings.add(holdingsRecord);
        Mockito.when(bibRecord.getHoldings()).thenReturn(holdings);
        List<Holding> holdingList=new ArrayList<>();
        holdingList.add(holding);
        Mockito.when(holdingsRecord.getHolding()).thenReturn(holdingList);
        List<Items> items=new ArrayList<>();
        Mockito.when(holding.getItems()).thenReturn(items);
        ItemEntity itemEntity=mockSCSBFormatResolver.getItemEntityFromRecord(bibRecords,3);
        assertNull(itemEntity);
    }

    @Test
    public void unmarshal() throws Exception {
        Mockito.when(commonUtil.getBibRecordsForSCSBFormat(Mockito.anyString())).thenReturn(bibRecords);
        Object bibRecord=mockSCSBFormatResolver.unmarshal("test");
        assertNotNull(bibRecord);
    }

    @Test
    public void isFormat() throws Exception {
        boolean format=mockSCSBFormatResolver.isFormat("SCSB");
        assertTrue(format);
    }

    @Test
    public void getBibData() throws Exception {
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setIlsBibdataApiAuth("test");
        ilsConfigProperties.setIlsBibdataApiEndpoint("test");
        ilsConfigProperties.setIlsBibdataApiParameter("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(bibDataFactory.getAuth(Mockito.anyString())).thenReturn(bibDataForAccessionInterface);
        Mockito.when(bibDataForAccessionInterface.getBibData(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(RecapCommonConstants.SUCCESS);
        String getBibData=mockSCSBFormatResolver.getBibData("123456","NA","NYPL");
        assertEquals(RecapCommonConstants.SUCCESS,getBibData);
    }

    private Set<AccessionResponse> getAccessionResponses() {
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        AccessionResponse accessionResponse=new AccessionResponse();
        accessionResponse.setMessage("test");
        accessionResponse.setItemBarcode("123");
        accessionResponses.add(accessionResponse);
        return accessionResponses;
    }

}
