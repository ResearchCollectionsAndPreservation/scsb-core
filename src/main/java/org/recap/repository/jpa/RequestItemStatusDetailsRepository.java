package org.recap.repository.jpa;


import org.recap.model.jpa.RequestStatusEntity;

import java.util.List;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface RequestItemStatusDetailsRepository extends BaseRepository<RequestStatusEntity> {

    /**
     * Find by request status code request status entity.
     *
     * @param requestStatusCode the request status code
     * @return the request status entity
     */
    RequestStatusEntity findByRequestStatusCode(String requestStatusCode);

    List<RequestStatusEntity> findByRequestStatusCodeIn(List<String> requestStatusCode);
}
