package org.recap.util;

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
import org.recap.model.jpa.*;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemBarcodeHistoryDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.service.accession.AccessionInterface;
import org.recap.service.accession.AccessionResolverFactory;
import org.recap.spring.SwaggerAPIProvider;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccessionProcessServiceUT extends BaseTestCaseUT {

    @InjectMocks
    AccessionProcessService accessionProcessService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    AccessionUtil accessionUtil;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    AccessionResolverFactory accessionResolverFactory;

    @Mock
    AccessionInterface formatResolver;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    ItemBarcodeHistoryDetailsRepository itemBarcodeHistoryDetailsRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    SwaggerAPIProvider swaggerAPIProvider;

    @Mock
    Exception ex;

    @Test
    public void callCheckinException(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setDeleted(true);
        itemEntity.setBarcode("12345");
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.doCallRealMethod().when(accessionUtil).setAccessionResponse(Mockito.anySet(),Mockito.anyString(),Mockito.anyString());
        ReflectionTestUtils.setField(accessionUtil,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        Mockito.doCallRealMethod().when(accessionUtil).saveItemChangeLogEntity(Mockito.anyString(),Mockito.anyString(),Mockito.anyList());
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenThrow(NullPointerException.class);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void callCheckinRestClientException(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setDeleted(true);
        itemEntity.setBarcode("12345");
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.doCallRealMethod().when(accessionUtil).setAccessionResponse(Mockito.anySet(),Mockito.anyString(),Mockito.anyString());
        ReflectionTestUtils.setField(accessionUtil,"itemChangeLogDetailsRepository",itemChangeLogDetailsRepository);
        Mockito.doCallRealMethod().when(accessionUtil).saveItemChangeLogEntity(Mockito.anyString(),Mockito.anyString(),Mockito.anyList());
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenThrow(RestClientException.class);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void processRecordsAlreadyAccessioned(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("12345");
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("111");
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        HoldingsEntity holdingsEntity=new HoldingsEntity();
        holdingsEntity.setOwningInstitutionHoldingsId("222");
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void processRecordsAccessioned(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("12345");
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void processRecordsAccession(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        Map<String, String> responseMap1=new HashMap<>();
        responseMap1.put(RecapCommonConstants.REASON_FOR_BIB_FAILURE,RecapCommonConstants.REASON_FOR_BIB_FAILURE);
        Map<String, String> responseMap=new HashMap<>();
        responseMap.put(RecapCommonConstants.REASON_FOR_ITEM_FAILURE,RecapCommonConstants.REASON_FOR_ITEM_FAILURE);
        responseMaps.add(responseMap);
        responseMaps.add(responseMap1);
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("12345");
        itemEntity.setInstitutionEntity(institutionEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        ilsConfigProperties.setIlsRefileEndpointProtocol("REST");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(formatResolver.getBibData(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("test");
        Mockito.when(accessionResolverFactory.getFormatResolver(Mockito.anyString())).thenReturn(formatResolver);
        Mockito.when(formatResolver.getItemEntityFromRecord(null,null)).thenReturn(itemEntity);
        List<InstitutionEntity> institutionEntities =new ArrayList<>();
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void removeDuplicateRecord(){
        List<AccessionRequest> trimmedAccessionRequests=new ArrayList<>();
        List<AccessionRequest> removeDuplicateRecord=accessionProcessService.removeDuplicateRecord(trimmedAccessionRequests);
        assertNotNull(removeDuplicateRecord);
    }

    @Test
    public void getInstitutionIdCodeMapException(){
        Mockito.when(institutionDetailsRepository.findAll()).thenThrow(NullPointerException.class);
        Map<String,Integer> getInstitutionIdCodeMap=accessionProcessService.getInstitutionIdCodeMap();
        assertNotNull(getInstitutionIdCodeMap);
    }

    @Test
    public void getBibData(){
        AccessionRequest accessionRequest=new AccessionRequest();
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        Mockito.when(formatResolver.getBibData(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenThrow(NullPointerException.class);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        String bibData=accessionProcessService.getBibData(accessionResponses,accessionRequest,reportDataEntitys,"PUL","PA","123456",formatResolver,imsLocationEntity);

    }

    @Test
    public void processRecordsAccessionException(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        Map<String, String> responseMap1=new HashMap<>();
        responseMap1.put(RecapCommonConstants.REASON_FOR_BIB_FAILURE,RecapCommonConstants.REASON_FOR_BIB_FAILURE);
        Map<String, String> responseMap=new HashMap<>();
        responseMap.put(RecapCommonConstants.REASON_FOR_ITEM_FAILURE,RecapCommonConstants.REASON_FOR_ITEM_FAILURE);
        responseMaps.add(responseMap);
        responseMaps.add(responseMap1);
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("12345");
        itemEntity.setInstitutionEntity(institutionEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        ilsConfigProperties.setIlsRefileEndpointProtocol("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(formatResolver.getBibData(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("test");
        Mockito.when(accessionResolverFactory.getFormatResolver(Mockito.anyString())).thenReturn(formatResolver);
        Mockito.when(formatResolver.getItemEntityFromRecord(null,null)).thenReturn(itemEntity);
        List<InstitutionEntity> institutionEntities =new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void processRecordsException(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("12345");
        itemEntity.setInstitutionEntity(institutionEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties).thenThrow(NullPointerException.class);
        Mockito.when(formatResolver.getBibData(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("test");
        Mockito.when(accessionResolverFactory.getFormatResolver(Mockito.anyString())).thenReturn(formatResolver);
        Mockito.when(formatResolver.getItemEntityFromRecord(null,null)).thenReturn(itemEntity);
        List<InstitutionEntity> institutionEntities =new ArrayList<>();
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        Mockito.when(itemBarcodeHistoryDetailsRepository.save(Mockito.any())).thenThrow(NullPointerException.class);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void processException(){
        String[] errors={RecapConstants.ITEM_BARCODE_NOT_FOUND,RecapConstants.MARC_FORMAT_PARSER_ERROR};
        for (String error: errors) {
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        Mockito.when(ex.getMessage()).thenReturn(error);
            ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
            accessionProcessService.processException(new HashSet<>(),accessionRequest,new ArrayList<>(),"PUL",imsLocationEntity,ex);
        assertNotNull(accessionRequest);
        }
    }
    @Test
    public void processRecordsAccessionedProcess(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("12345");
        itemEntity.setInstitutionEntity(institutionEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties).thenThrow(NullPointerException.class);
        Mockito.when(formatResolver.getBibData(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("test");
        Mockito.when(accessionResolverFactory.getFormatResolver(Mockito.anyString())).thenReturn(formatResolver);
        Mockito.when(formatResolver.getItemEntityFromRecord(null,null)).thenReturn(itemEntity);
        List<InstitutionEntity> institutionEntities =new ArrayList<>();
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        Mockito.when(itemBarcodeHistoryDetailsRepository.save(Mockito.any())).thenThrow(NullPointerException.class);
        Mockito.when(formatResolver.isAccessionProcess(Mockito.any(),Mockito.anyString())).thenReturn(true);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",true,imsLocationEntity);
        assertEquals(accessionResponses,accessionResponse);
    }

    @Test
    public void processRecordsException1(){
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        List<Map<String, String>> responseMaps=new ArrayList<>();
        AccessionRequest accessionRequest=new AccessionRequest();
        accessionRequest.setItemBarcode("12345");
        accessionRequest.setCustomerCode("PA");
        List<ReportDataEntity> reportDataEntitys=new ArrayList<>();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        List<ItemEntity> itemEntities=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntity.setBarcode("12345");
        itemEntity.setInstitutionEntity(institutionEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(accessionUtil.reAccessionItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        Mockito.when(accessionUtil.indexReaccessionedItem(Mockito.anyList())).thenReturn(RecapCommonConstants.SUCCESS);
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties).thenThrow(NullPointerException.class);
        Mockito.when(accessionResolverFactory.getFormatResolver(Mockito.anyString())).thenReturn(formatResolver);
        Mockito.when(formatResolver.getItemEntityFromRecord(null,null)).thenReturn(itemEntity);
        List<InstitutionEntity> institutionEntities =new ArrayList<>();
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        Mockito.when(itemBarcodeHistoryDetailsRepository.save(Mockito.any())).thenThrow(NullPointerException.class);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        Object accessionResponse=accessionProcessService.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,"PUL",false,imsLocationEntity);
        assertNotNull(accessionResponse);
    }
}
