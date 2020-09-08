package org.recap.repository.jpa;

import org.recap.model.jpa.OwningInstitutionIDSequence;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by premkb on 16/8/17.
 */
public interface OwningInstitutionIDSequenceRepository extends JpaRepository<OwningInstitutionIDSequence, Integer> {

}

