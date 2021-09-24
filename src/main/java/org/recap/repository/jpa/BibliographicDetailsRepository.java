package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends BaseRepository<BibliographicEntity>,BibliographicDetailsResopistoryCustom {

    /**
     * Find by owning institution bib id list.
     *
     * @param owningInstitutionBibId the owning institution bib id
     * @return the list
     */
    List<BibliographicEntity> findByOwningInstitutionBibId(String owningInstitutionBibId);

    /**
     * Gets non deleted items count.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the non deleted items count
     */
    @Query(value = "SELECT COUNT(*) FROM ITEM_T, BIBLIOGRAPHIC_ITEM_T, BIBLIOGRAPHIC_T WHERE " +
            "BIBLIOGRAPHIC_T.BIBLIOGRAPHIC_ID = BIBLIOGRAPHIC_ITEM_T.BIBLIOGRAPHIC_ID AND ITEM_T.ITEM_ID = BIBLIOGRAPHIC_ITEM_T.ITEM_ID " +
            "AND ITEM_T.IS_DELETED = 0 AND " +
            "BIBLIOGRAPHIC_T.OWNING_INST_BIB_ID = :owningInstitutionBibId AND BIBLIOGRAPHIC_T.OWNING_INST_ID = :owningInstitutionId", nativeQuery = true)
    Long getNonDeletedItemsCount(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

    /**
     * Mark bibs as deleted int.
     *
     * @param bibliographicIds the bibliographic ids
     * @param lastUpdatedBy    the last updated by
     * @param lastUpdatedDate  the last updated date
     * @return the int
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE BibliographicEntity bib SET bib.isDeleted = true, bib.lastUpdatedBy = :lastUpdatedBy, bib.lastUpdatedDate = :lastUpdatedDate WHERE bib.id IN :bibliographicIds")
    int markBibsAsDeleted(@Param("bibliographicIds") List<Integer> bibliographicIds, @Param("lastUpdatedBy") String lastUpdatedBy, @Param("lastUpdatedDate") Date lastUpdatedDate);

    /**
     * Mark bibs as not deleted int.
     *
     * @param bibliographicIds the bibliographic ids
     * @param lastUpdatedBy    the last updated by
     * @param lastUpdatedDate  the last updated date
     * @return the int
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE BibliographicEntity bib SET bib.isDeleted = false, bib.lastUpdatedBy = :lastUpdatedBy, bib.lastUpdatedDate = :lastUpdatedDate WHERE bib.id IN :bibliographicIds")
    int markBibsAsNotDeleted(@Param("bibliographicIds") List<Integer> bibliographicIds, @Param("lastUpdatedBy") String lastUpdatedBy, @Param("lastUpdatedDate") Date lastUpdatedDate);

    /**
     * Find bibliographic entity by using owning institution id and owning institution bib id.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the bibliographic entity
     */
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibId(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

    /**
     * Find the bibliographic entity by using owning institution id,owning institution bib id and is deleted field which has false value.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the bibliographic entity
     */
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Integer owningInstitutionId, String owningInstitutionBibId);

    /**
     * Find by matchingIdentity list.
     *
     * @param matchingIdentity the matching Identifier
     * @return the list
     */
    List<BibliographicEntity> findByMatchingIdentity(String matchingIdentity);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE `BIBLIOGRAPHIC_T` SET `MA_QUALIFIER`=:maQualifier WHERE `BIBLIOGRAPHIC_ID` in (:bibliographicIds)",nativeQuery = true)
    int updateMaQualifier(@Param("bibliographicIds") Set<Integer> bibIds, @Param("maQualifier") Integer maQualifier);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE `BIBLIOGRAPHIC_T` SET `MATCHING_IDENTITY`=null, `MATCH_SCORE`=0, `ANAMOLY_FLAG`=0, `MA_QUALIFIER`=:maQualifier WHERE `BIBLIOGRAPHIC_ID` in (:bibliographicIds)",nativeQuery = true)
    int resetMatchingColumnsAndUpdateMaQualifier(@Param("bibliographicIds") Set<Integer> bibIds,  @Param("maQualifier") Integer maQualifier);

    @Query(value = "SELECT BIBLIOGRAPHIC_ID FROM BIBLIOGRAPHIC_T WHERE MATCHING_IDENTITY = :matchingIdentity", nativeQuery = true)
    Set<Integer> findIdByMatchingIdentity(@Param("matchingIdentity") String matchingIdentity);

    @Query(value = "SELECT bib.BIBLIOGRAPHIC_ID, item.COLLECTION_GROUP_ID FROM BIBLIOGRAPHIC_T bib " +
            "left outer join BIBLIOGRAPHIC_ITEM_T bibItem ON bibItem.BIBLIOGRAPHIC_ID = bib.BIBLIOGRAPHIC_ID " +
            "left outer join ITEM_T item ON item.ITEM_ID = bibItem.ITEM_ID " +
            "WHERE bib.MATCHING_IDENTITY = :matchingIdentity and bib.IS_DELETED = 0 and bib.CATALOGING_STATUS = 'Complete' " +
            "and item.IS_DELETED = 0 and item.CATALOGING_STATUS = 'Complete'", nativeQuery = true)
    List<Object[]> findBibIdAndCgdIdByMatchingIdentity(@Param("matchingIdentity") String matchingIdentity);


}
