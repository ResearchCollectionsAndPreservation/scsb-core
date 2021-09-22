package org.recap.service.submitcollection.callable;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.recap.ScsbCommonConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by rajeshbabuk on 17/Sep/2021
 */
@Slf4j
@Component
@Scope("prototype")
@Data
public class SubmitCollectionMatchPointsCheckCallable implements Callable<Map<Integer, Set<Integer>>> {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    Integer fetchedBibId;
    String incomingMarcXml;
    String existingMarcXml;
    String matchingIdentifier;
    String institutionCode;
    boolean isCGDProtected;
    Map<String, ItemEntity> fetchedBarcodeItemEntityMap;
    Map<String, ItemEntity> incomingBarcodeItemEntityMap;
    Map<Integer, String> collectionGroupIdCodeMap;
    Map<Integer, String> itemStatusIdCodeMap;

    @Override
    public Map<Integer, Set<Integer>> call() throws Exception {
        Map<Integer, Set<Integer>> responseMap = new HashMap<>();
        try {
            Set<Integer> bibIds = new HashSet<>();
            boolean isMatchPointsChanged = commonUtil.checkIfMatchPointsChanged(incomingMarcXml, existingMarcXml, institutionCode);
            boolean isCgdChangedToShared = !isCGDProtected && commonUtil.isCgdChangedToShared(fetchedBarcodeItemEntityMap, incomingBarcodeItemEntityMap, collectionGroupIdCodeMap, itemStatusIdCodeMap, false);
            boolean isCgdAlreadyShared = commonUtil.isCgdChangedToShared(fetchedBarcodeItemEntityMap, incomingBarcodeItemEntityMap, collectionGroupIdCodeMap, itemStatusIdCodeMap, true);
            int maQualifier = 0;
            if (isMatchPointsChanged && (isCgdAlreadyShared || isCgdChangedToShared)) {
                maQualifier = ScsbCommonConstants.MA_QUALIFIER_3;
            } else if (isCgdChangedToShared) {
                maQualifier = ScsbCommonConstants.MA_QUALIFIER_2;
            } else if (isMatchPointsChanged) {
                maQualifier = ScsbCommonConstants.MA_QUALIFIER_1;
            }
            if (maQualifier > 0) {
                bibIds = getBibIdsBasedOnMatchingIdValue(bibIds, maQualifier != ScsbCommonConstants.MA_QUALIFIER_2);
                putToResponseMap(maQualifier, bibIds, responseMap);
                log.info("Matching Id - {}, Update MA Qualifier to {}, Collected {} Bib Ids: {}", matchingIdentifier, maQualifier, bibIds.size(), bibIds);
            }
        } catch (Exception e) {
            log.info("Exception while processing SC Match Points Check in thread for Fetched Bib Id: {} - {}", fetchedBibId, e.getMessage());
            e.printStackTrace();
        }
        return responseMap;
    }

    private Set<Integer> getBibIdsBasedOnMatchingIdValue(Set<Integer> bibIds, boolean getMatchedBibs) {
        if (matchingIdentifier != null && getMatchedBibs) {
            bibIds = bibliographicDetailsRepository.findIdByMatchingIdentity(matchingIdentifier);
        } else {
            bibIds.add(fetchedBibId);
        }
        return bibIds;
    }

    private void putToResponseMap(Integer maQualifier, Set<Integer> bibIds, Map<Integer, Set<Integer>> responseMap) {
        if (responseMap.containsKey(maQualifier)) {
            responseMap.get(maQualifier).addAll(bibIds);
        } else {
            responseMap.put(maQualifier, bibIds);
        }
    }
}
