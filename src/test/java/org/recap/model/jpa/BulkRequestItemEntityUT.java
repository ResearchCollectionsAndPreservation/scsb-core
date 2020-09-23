package org.recap.model.jpa;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class BulkRequestItemEntityUT {

    @Test
    public void getBulkRequestItemEntity(){
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        assertNotNull(institutionEntity.getId());
        assertNotNull(institutionEntity.getInstitutionCode());
        assertNotNull(institutionEntity.getInstitutionName());
        bulkRequestItemEntity.setInstitutionEntity(institutionEntity);
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setBulkRequestItemEntity(bulkRequestItemEntity);
        assertNotNull(requestItemEntity.getBulkRequestItemEntity());
        bulkRequestItemEntity.setRequestItemEntities(Arrays.asList(requestItemEntity));
        assertNotNull(bulkRequestItemEntity.getInstitutionEntity());
        assertNotNull(bulkRequestItemEntity.getRequestItemEntities());
    }
}
