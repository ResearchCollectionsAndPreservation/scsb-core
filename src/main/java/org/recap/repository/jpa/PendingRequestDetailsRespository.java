package org.recap.repository.jpa;

import org.recap.model.jpa.PendingRequestEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingRequestDetailsRespository extends BaseRepository<PendingRequestEntity> {

    List<PendingRequestEntity> save(List<RequestItemEntity> pendingRequestItemEntities);

    @Query(value = "select * from pending_request_t where request_id IN :requestIds",nativeQuery = true)
    List<PendingRequestEntity> findByRequestIds(@Param("requestIds") List<Integer> requestIds);
}
