package org.recap.service.accession.callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.service.submitcollection.callable.SubmitCollectionMatchPointsCheckCallable;
import org.recap.util.CommonUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

public class SubmitCollectionMatchPointsCheckCallableUT extends BaseTestCaseUT {

    @InjectMocks
    SubmitCollectionMatchPointsCheckCallable submitCollectionMatchPointsCheckCallable;

    @Mock
    private CommonUtil commonUtil;

    @Mock
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    Integer fetchedBibId = 23553;
    Integer fetchedBibMAQualifier = 1;
    String incomingMarcXml = "test";
    String existingMarcXml = "test";
    String matchingIdentifier = "23445";
    String institutionCode = "PUL";
    boolean isCGDProtected = false;
    Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
    Map<String, ItemEntity> incomingBarcodeItemEntityMap = new HashMap<>();
    Map<Integer, String> collectionGroupIdCodeMap = new HashMap<>();
    Map<Integer, String> itemStatusIdCodeMap = new HashMap<>();

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "fetchedBibId", fetchedBibId);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "fetchedBibMAQualifier", fetchedBibMAQualifier);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "incomingMarcXml", incomingMarcXml);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "existingMarcXml", existingMarcXml);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "matchingIdentifier", matchingIdentifier);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "institutionCode", institutionCode);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "isCGDProtected", isCGDProtected);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "fetchedBarcodeItemEntityMap", fetchedBarcodeItemEntityMap);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "incomingBarcodeItemEntityMap", incomingBarcodeItemEntityMap);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "collectionGroupIdCodeMap", collectionGroupIdCodeMap);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "itemStatusIdCodeMap", itemStatusIdCodeMap);
    }

    @Test
    public void call() throws Exception {
        List<Object[]> bibIdAndCgdIdByMatchingIdentityObjectList = new ArrayList<>();
        Object[] bibliographicEntity = {1, 2};
        bibIdAndCgdIdByMatchingIdentityObjectList.add(bibliographicEntity);
        ReflectionTestUtils.setField(commonUtil, "bibliographicDetailsRepository", bibliographicDetailsRepository);
        Mockito.when(bibliographicDetailsRepository.findBibIdAndCgdIdByMatchingIdentity(any())).thenReturn(bibIdAndCgdIdByMatchingIdentityObjectList);
        Mockito.when(commonUtil.checkIfMatchPointsChanged(any(), any(), any())).thenReturn(Boolean.TRUE);
        Mockito.when(commonUtil.isCgdChangedToShared(any(), any(), any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(commonUtil.isCgdAlreadyShared(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        Mockito.doCallRealMethod().when(commonUtil).collectSharedAndNonSharedBibIdsForMatchingId(any(), any(), any(), any());
        Map<Integer, Set<Integer>> setMap = submitCollectionMatchPointsCheckCallable.call();
        assertNotNull(setMap);
    }

    @Test
    public void callWithoutMatchPointsChanged() throws Exception {
        List<Object[]> bibIdAndCgdIdByMatchingIdentityObjectList = new ArrayList<>();
        Object[] bibliographicEntity = {1, 2};
        bibIdAndCgdIdByMatchingIdentityObjectList.add(bibliographicEntity);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "matchingIdentifier", "");
        ReflectionTestUtils.setField(commonUtil, "bibliographicDetailsRepository", bibliographicDetailsRepository);
        Mockito.when(bibliographicDetailsRepository.findBibIdAndCgdIdByMatchingIdentity(any())).thenReturn(bibIdAndCgdIdByMatchingIdentityObjectList);
        Mockito.when(commonUtil.checkIfMatchPointsChanged(any(), any(), any())).thenReturn(Boolean.FALSE);
        Mockito.when(commonUtil.isCgdChangedToShared(any(), any(), any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(commonUtil.isCgdAlreadyShared(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        Mockito.doCallRealMethod().when(commonUtil).collectSharedAndNonSharedBibIdsForMatchingId(any(), any(), any(), any());
        Map<Integer, Set<Integer>> setMap = submitCollectionMatchPointsCheckCallable.call();
        assertNotNull(setMap);
    }

    @Test
    public void callWithoutCgdAlreadySharedAndCgdChangedToShared() throws Exception {
        List<Object[]> bibIdAndCgdIdByMatchingIdentityObjectList = new ArrayList<>();
        Object[] bibliographicEntity = {1, 2};
        bibIdAndCgdIdByMatchingIdentityObjectList.add(bibliographicEntity);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "matchingIdentifier", "");
        ReflectionTestUtils.setField(commonUtil, "bibliographicDetailsRepository", bibliographicDetailsRepository);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "fetchedBibMAQualifier", 0);
        Mockito.when(bibliographicDetailsRepository.findBibIdAndCgdIdByMatchingIdentity(any())).thenReturn(bibIdAndCgdIdByMatchingIdentityObjectList);
        Mockito.when(commonUtil.checkIfMatchPointsChanged(any(), any(), any())).thenReturn(Boolean.TRUE);
        Mockito.when(commonUtil.isCgdChangedToShared(any(), any(), any(), any(), anyBoolean())).thenReturn(Boolean.FALSE);
        Mockito.when(commonUtil.isCgdAlreadyShared(any(), any(), any(), any())).thenReturn(Boolean.FALSE);
        Mockito.doCallRealMethod().when(commonUtil).collectSharedAndNonSharedBibIdsForMatchingId(any(), any(), any(), any());
        Map<Integer, Set<Integer>> setMap = submitCollectionMatchPointsCheckCallable.call();
        assertNotNull(setMap);
    }

    @Test
    public void callException() throws Exception {
        List<Object[]> bibIdAndCgdIdByMatchingIdentityObjectList = new ArrayList<>();
        Object[] bibliographicEntity = {1, 2};
        bibIdAndCgdIdByMatchingIdentityObjectList.add(bibliographicEntity);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "matchingIdentifier", "");
        ReflectionTestUtils.setField(commonUtil, "bibliographicDetailsRepository", bibliographicDetailsRepository);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "fetchedBibMAQualifier", 0);
        Mockito.doThrow(new NullPointerException()).when(commonUtil).checkIfMatchPointsChanged(any(), any(), any());
        Map<Integer, Set<Integer>> setMap = submitCollectionMatchPointsCheckCallable.call();
        assertNotNull(setMap);
    }

    @Test
    public void setBibIdsByMatchingIdToMap() {
        int maQualifier = ScsbCommonConstants.MA_QUALIFIER_1;
        Set<Integer> bibIds = new HashSet<>();
        bibIds.add(1);
        Map<Integer, Set<Integer>> responseMap = new HashMap<>();
        responseMap.put(1, bibIds);
        Mockito.when(bibliographicDetailsRepository.findIdByMatchingIdentity(any())).thenReturn(bibIds);
        ReflectionTestUtils.invokeMethod(submitCollectionMatchPointsCheckCallable, "setBibIdsByMatchingIdToMap", maQualifier, responseMap, bibIds);
    }

    @Test
    public void setBibIdsByMatchingIdToMapMA_QUALIFIER_2() {
        int maQualifier = ScsbCommonConstants.MA_QUALIFIER_2;
        Set<Integer> bibIds = new HashSet<>();
        bibIds.add(1);
        Map<Integer, Set<Integer>> responseMap = new HashMap<>();
        responseMap.put(2, bibIds);
        ReflectionTestUtils.setField(submitCollectionMatchPointsCheckCallable, "fetchedBibMAQualifier", 0);
        Mockito.when(bibliographicDetailsRepository.findIdByMatchingIdentity(any())).thenReturn(bibIds);
        ReflectionTestUtils.invokeMethod(submitCollectionMatchPointsCheckCallable, "setBibIdsByMatchingIdToMap", maQualifier, responseMap, bibIds);
    }
}
