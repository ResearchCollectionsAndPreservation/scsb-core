package org.recap.model.jpa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class HoldingsEntityUT {

    @Test
    public  void getHoldingsEntity(){

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        assertNotNull(institutionEntity);
        List<BibliographicEntity> bibliographicEntities =  new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setInstitutionEntity(institutionEntity);
        bibliographicEntities.add(bibliographicEntity);
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setInstitutionEntity(institutionEntity);
        holdingsEntity.setBibliographicEntities(bibliographicEntities);
        holdingsEntity.setOwningInstitutionHoldingsId("113t56");
        holdingsEntity.hashCode();
        holdingsEntity.equals(holdingsEntity);
        HoldingsEntity holdingsEntity1 = new HoldingsEntity();
        holdingsEntity1.setOwningInstitutionHoldingsId("235456");
        holdingsEntity1.equals(holdingsEntity);
    }
}
