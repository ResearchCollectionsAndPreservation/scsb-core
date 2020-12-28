package org.recap.repository.jpa;

import org.recap.model.jpa.ImsLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImsLocationDetailsRepository extends JpaRepository<ImsLocationEntity, Integer> {
    ImsLocationEntity findByImsLocationCode(String imsLocationCode);
}