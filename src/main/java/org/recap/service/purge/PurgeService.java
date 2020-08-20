package org.recap.service.purge;

import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.repository.jpa.AccessionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hemalathas on 13/4/17.
 */
@Service
public class PurgeService {

    private static final Logger logger = LoggerFactory.getLogger(PurgeService.class);

    @Value("${purge.email.address.edd.request.day.limit}")
    private Integer purgeEmailEddRequestDayLimit;

    @Value("${purge.email.address.physical.request.day.limit}")
    private Integer purgeEmailPhysicalRequestDayLimit;

    @Value("${purge.exception.request.day.limit}")
    private Integer purgeExceptionRequestDayLimit;

    @Value("${purge.accession.request.day.limit}")
    private Integer purgeAccessionRequestDayLimit;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    private RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    private AccessionDetailsRepository accessionDetailsRepository;

    /**
     * Purge email address map.
     *
     * @return the map
     */
    public Map<String, String> purgeEmailAddress() {
        Map<String, String> responseMap = new HashMap<>();
        try {
            List<RequestTypeEntity> requestTypeEntityList = requestTypeDetailsRepository.findAll();
            List<Integer> physicalRequestTypeIdList = new ArrayList<>();
            List<Integer> eddRequestTypeIdList = new ArrayList<>();
            for (RequestTypeEntity requestTypeEntity : requestTypeEntityList) {
                if (requestTypeEntity.getRequestTypeCode().equals(RecapConstants.EDD_REQUEST)) {
                    eddRequestTypeIdList.add(requestTypeEntity.getId());
                } else {
                    physicalRequestTypeIdList.add(requestTypeEntity.getId());
                }
            }
            int noOfUpdatedRecordsForEddRequest = requestItemDetailsRepository.purgeEmailId(eddRequestTypeIdList, new Date(), purgeEmailEddRequestDayLimit, RecapConstants.REFILED_REQUEST);
            int noOfUpdatedRecordsForPhysicalRequest = requestItemDetailsRepository.purgeEmailId(physicalRequestTypeIdList, new Date(), purgeEmailPhysicalRequestDayLimit, RecapConstants.REFILED_REQUEST);
            responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.SUCCESS);
            responseMap.put(RecapCommonConstants.PURGE_EDD_REQUEST, String.valueOf(noOfUpdatedRecordsForEddRequest));
            responseMap.put(RecapCommonConstants.PURGE_PHYSICAL_REQUEST, String.valueOf(noOfUpdatedRecordsForPhysicalRequest));
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.FAILURE);
            responseMap.put(RecapCommonConstants.MESSAGE, exception.getMessage());
        }
        return responseMap;
    }

    /**
     * Purge exception Request from Request_t table after certain period.
     *
     * @return the map
     */
    public Map<String, String> purgeExceptionRequests() {
        Map<String, String> responseMap = new HashMap<>();
        try {
            Integer countOfPurgedExceptionRequests = requestItemDetailsRepository.purgeExceptionRequests(RecapConstants.REQUEST_STATUS_EXCEPTION, new Date(), purgeExceptionRequestDayLimit);
            logger.info("Total number of exception requests purged : {}", countOfPurgedExceptionRequests);
            responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.SUCCESS);
            responseMap.put(RecapCommonConstants.MESSAGE, RecapConstants.COUNT_OF_PURGED_EXCEPTION_REQUESTS + " : " + countOfPurgedExceptionRequests);
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.FAILURE);
            responseMap.put(RecapCommonConstants.MESSAGE, exception.getMessage());
        }
        return responseMap;
    }

    /**
     * Purge accession requests map.
     *
     * @return the map
     */
    public Map<String, String> purgeAccessionRequests() {
        Map<String, String> responseMap = new HashMap<>();
        try {
            Integer countOfPurgedAccessionRequests = accessionDetailsRepository.purgeAccessionRequests(RecapConstants.COMPLETE, new Date(), purgeAccessionRequestDayLimit);
            logger.info("Total number of accession requests purged : {}", countOfPurgedAccessionRequests);
            responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.SUCCESS);
            responseMap.put(RecapCommonConstants.MESSAGE, RecapConstants.COUNT_OF_PURGED_ACCESSION_REQUESTS + " : " + countOfPurgedAccessionRequests);
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.FAILURE);
            responseMap.put(RecapCommonConstants.MESSAGE, exception.getMessage());
        }
        return responseMap;
    }
}
