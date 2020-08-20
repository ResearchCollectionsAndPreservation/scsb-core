package org.recap.repository.jpa;

import org.recap.model.jpa.CollectionGroupEntity;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface CollectionGroupDetailsRepository extends BaseRepository<CollectionGroupEntity> {

    /**
     * Find by collection group code collection group entity.
     *
     * @param collectionGroupCode the collection group code
     * @return the collection group entity
     */
    CollectionGroupEntity findByCollectionGroupCode(String collectionGroupCode);
}
