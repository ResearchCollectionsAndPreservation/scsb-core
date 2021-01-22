package org.recap.repository.jpa;

import org.recap.model.jpa.ImsLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImsLocationDetailsRepository extends JpaRepository<ImsLocationEntity, Integer> {
    ImsLocationEntity findByImsLocationCode(String imsLocationCode);

    @Query(value = "select IMS_LOCATION_CODE from ims_location_t", nativeQuery = true)
    List<String> findAllImsLocationCodes();

    @Query(value = "select IMS_LOCATION_CODE from ims_location_t where IMS_LOCATION_CODE != 'UN';",nativeQuery = true)
    List<String> findAllImsLocationCodeExceptUN();
}