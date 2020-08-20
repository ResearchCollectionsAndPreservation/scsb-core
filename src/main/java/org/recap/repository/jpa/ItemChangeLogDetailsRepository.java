package org.recap.repository.jpa;


import org.recap.model.jpa.ItemChangeLogEntity;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public interface ItemChangeLogDetailsRepository extends BaseRepository<ItemChangeLogEntity> {

     /**
      * Find by record id and operation type item change log entity.
      *
      * @param recordId      the record id
      * @param operationType the operation type
      * @return the item change log entity
      */
     ItemChangeLogEntity findByRecordIdAndOperationType(Integer recordId, String operationType);
}
