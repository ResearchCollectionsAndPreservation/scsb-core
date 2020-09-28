package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.OwningInstitutionIDSequence;
import org.recap.repository.jpa.OwningInstitutionIDSequenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 16/8/17.
 */
public class OwningInstitutionIDSequenceRepositoryUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(OwningInstitutionIDSequenceRepositoryUT.class);

    @Autowired
    private OwningInstitutionIDSequenceRepository owningInstitutionIDSequenceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void generateValue(){
        OwningInstitutionIDSequence owningInstitutionIDSequence = new OwningInstitutionIDSequence();
        OwningInstitutionIDSequence savedOwningInstitutionIDSequence = owningInstitutionIDSequenceRepository.saveAndFlush(owningInstitutionIDSequence);
        entityManager.refresh(savedOwningInstitutionIDSequence);
        logger.info("id--->"+savedOwningInstitutionIDSequence.getID());
        assertNotNull(savedOwningInstitutionIDSequence.getID());
    }
}
