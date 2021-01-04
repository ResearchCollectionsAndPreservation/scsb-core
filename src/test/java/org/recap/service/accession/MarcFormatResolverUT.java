package org.recap.service.accession;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.AccessionUtil;
import org.recap.util.CommonUtil;
import org.recap.util.MarcUtil;
import org.recap.util.PropertyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MarcFormatResolverUT extends BaseTestCaseUT {

    @InjectMocks
    MarcFormatResolver mockMarcFormatResolver;

    @Mock
    AccessionValidationService accessionValidationService;

    @Mock
    Record record;

    @Mock
    CommonUtil commonUtil;

    @Mock
    AccessionUtil accessionUtil;

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
        List<Record> records=new ArrayList<>();
        records.add(record);
        records.add(record);
        Mockito.when(accessionValidationService.validateBoundWithMarcRecordFromIls(Mockito.anyList(),Mockito.any())).thenReturn(true);
        Mockito.when(commonUtil.getUpdatedDataResponse(Mockito.anySet(),Mockito.anyList(),Mockito.anyString(),Mockito.anyList(),Mockito.any(),Mockito.anyBoolean(),Mockito.anyInt(),Mockito.any(),Mockito.any())).thenReturn(RecapCommonConstants.SUCCESS);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        String response=mockMarcFormatResolver.processXml(getAccessionResponses(),records,responseMapList,"PUL",reportDataEntityList,accessionRequest,imsLocationEntity);
        assertEquals(RecapCommonConstants.SUCCESS,response);
    }

    @Test
    public void processXml() throws Exception {
        List<Map<String, String>> responseMapList=new ArrayList<>();
        List<ReportDataEntity> reportDataEntityList=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        List<Record> records=new ArrayList<>();
        records.add(record);
        records.add(record);
        Mockito.when(accessionValidationService.validateBoundWithMarcRecordFromIls(Mockito.anyList(),Mockito.any())).thenReturn(false);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        String response=mockMarcFormatResolver.processXml(getAccessionResponses(),records,responseMapList,"PUL",reportDataEntityList,accessionRequest,imsLocationEntity);
        assertEquals(RecapConstants.INVALID_BOUNDWITH_RECORD,response);
    }

    @Test
    public void getItemEntityFromRecord() throws Exception {
        List<Record> records=new ArrayList<>();
        records.add(record);
        Mockito.when(marcUtil.getDataFieldValue(records.get(0), "876", 'a')).thenReturn("1");
        ItemEntity itemEntity1=new ItemEntity();
        itemEntity1.setBarcode("123456");
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(itemEntity1);
        ItemEntity itemEntityFromRecord=mockMarcFormatResolver.getItemEntityFromRecord(records,1);
        assertEquals("123456",itemEntityFromRecord.getBarcode());
    }

    @Test
    public void getItemEntityFromRecordNull() throws Exception {
        List<Record> records=new ArrayList<>();
        records.add(null);
        Mockito.when(marcUtil.getDataFieldValue(records.get(0), "876", 'a')).thenReturn("1");
        ItemEntity itemEntity1=new ItemEntity();
        itemEntity1.setBarcode("123456");
        Mockito.when(itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(itemEntity1);
        ItemEntity itemEntityFromRecord=mockMarcFormatResolver.getItemEntityFromRecord(null,1);
        assertNull(itemEntityFromRecord);
    }

    @Test
    public void isFormat() throws Exception {
        boolean format=mockMarcFormatResolver.isFormat("MARC");
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
        String getBibData=mockMarcFormatResolver.getBibData("123456","CU","CUL");
        assertEquals(RecapCommonConstants.SUCCESS,getBibData);
    }

    @Test
    public void unmarshal() throws Exception {
        List<Record> records=new ArrayList<>();
        records.add(record);
        Mockito.when(commonUtil.marcRecordConvert(Mockito.anyString())).thenReturn(records);
        Object bibRecord=mockMarcFormatResolver.unmarshal("test");
        assertNotNull(bibRecord);
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
