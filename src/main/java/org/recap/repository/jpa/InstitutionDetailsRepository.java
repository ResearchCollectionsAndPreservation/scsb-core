package org.recap.repository.jpa;

import org.recap.model.jpa.InstitutionEntity;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface InstitutionDetailsRepository extends BaseRepository<InstitutionEntity> {

    /**
     * Find by institution code institution entity.
     *
     * @param institutionCode the institution code
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionCode(String institutionCode);

    /**
     * Find by institution name institution entity.
     *
     * @param institutionName the institution name
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionName(String institutionName);
}
