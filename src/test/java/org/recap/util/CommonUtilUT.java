package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CommonUtilUT {
    @InjectMocks
    CommonUtil commonUtil;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void buildHoldingsEntity(){
        BibliographicEntity bibliographicEntity= new BibliographicEntity();
        Date currentDate = new Date();
        StringBuilder errorMessage = new StringBuilder();
        String holdingsContent = null;
        HoldingsEntity holdingsEntity = commonUtil.buildHoldingsEntity(bibliographicEntity,currentDate,errorMessage,holdingsContent);
        assertNotNull(holdingsEntity);
    }
    @Test
    public void buildSubmitCollectionReportInfoAndAddFailures(){
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        List<SubmitCollectionReportInfo > failureSubmitCollectionReportInfoList = new ArrayList<>();
        failureSubmitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        String owningInstitution = "PUL";
        Map<String, ItemEntity > itemEntityMap = new HashMap<>();
        itemEntityMap.put("1",getBibliographicEntity().getItemEntities().get(0));
        Map.Entry<String, Map<String, ItemEntity >> incomingHoldingItemMapEntry = new AbstractMap.SimpleEntry<String, Map<String, ItemEntity>>("1", itemEntityMap);;
        ItemEntity incomingItemEntity = getBibliographicEntity().getItemEntities().get(0);
        commonUtil.buildSubmitCollectionReportInfoAndAddFailures(fetchedBibliographicEntity,failureSubmitCollectionReportInfoList,owningInstitution,incomingHoldingItemMapEntry,incomingItemEntity);
    }
    @Test
    public void buildSubmitCollectionReportInfoWhenNoGroupIdAndAddFailures(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList = new ArrayList<>();
        failureSubmitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        String owningInstitution = "PUL";
        ItemEntity incomingItemEntity = getBibliographicEntity().getItemEntities().get(0);
        commonUtil.buildSubmitCollectionReportInfoWhenNoGroupIdAndAddFailures(incomingBibliographicEntity,failureSubmitCollectionReportInfoList,owningInstitution,incomingItemEntity);
    }
    @Test
    public void addItemAndReportEntities(){
        List<ItemEntity> itemEntities = new ArrayList<>();
        List<ReportEntity > reportEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setId(1);
        boolean processHoldings = false;
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("itemEntity",getBibliographicEntity().getItemEntities().get(0));
        itemMap.put("itemReportEntity",reportEntity);
        commonUtil.addItemAndReportEntities(itemEntities,reportEntities,processHoldings,holdingsEntity,itemMap);
    }
    @Test
    public void rollbackUpdateItemAvailabilutyStatus(){
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        String userName = "Test";
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.AVAILABLE)).thenReturn(getItemStatusEntity());
        commonUtil.rollbackUpdateItemAvailabilutyStatus(itemEntity,userName);
    }
    @Test
    public void getItemStatusMap(){
        List<ItemStatusEntity> itemStatusEntities = new ArrayList<>();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        itemStatusEntities.add(itemStatusEntity);
        Mockito.when(itemStatusDetailsRepository.findAll()).thenReturn(itemStatusEntities);
        commonUtil.getItemStatusMap();
        commonUtil.getCollectionGroupMap();
        commonUtil.getInstitutionEntityMap();
        CommonUtil commonUtil = new CommonUtil();
        commonUtil.getItemStatusMap();
    }
    @Test
    public void getUser(){
        String user1 = commonUtil.getUser("1");
        String user2 = commonUtil.getUser("");
        assertNotNull(user1);
        assertEquals("1",user1);
        assertNotNull(user2);
        assertEquals("Discovery",user2);
    }
    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("SUCCESS");
        itemStatusEntity.setStatusDescription("AVAILABLE");
        return itemStatusEntity;
    }

    private SubmitCollectionReportInfo getSubmitCollectionReportInfo(){
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setOwningInstitution("PUL");
        submitCollectionReportInfo.setItemBarcode("123456");
        submitCollectionReportInfo.setCustomerCode("PA");
        submitCollectionReportInfo.setMessage("SUCCESS");
        return submitCollectionReportInfo;
    }
    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("12345");
        holdingsEntity.setDeleted(false);
        return holdingsEntity;
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

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);

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
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }
}
