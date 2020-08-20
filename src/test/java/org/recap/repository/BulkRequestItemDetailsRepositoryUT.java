package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.repository.jpa.BulkRequestItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 10/10/17.
 */
public class BulkRequestItemDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    BulkRequestItemDetailsRepository bulkRequestItemDetailsRepository;

    @Test
    public void testSaveBulkItemRequest() throws Exception {
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setBulkRequestName("TestFirstBulkRequest");
        bulkRequestItemEntity.setBulkRequestFileName("bulkItemUpload");
        bulkRequestItemEntity.setBulkRequestFileData("BARCODE\tCUSTOMER_CODE\n32101075852275\tPK".getBytes());
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setBulkRequestStatus(RecapConstants.PROCESSED);
        bulkRequestItemEntity.setCreatedBy("TestUser");
        bulkRequestItemEntity.setCreatedDate(new Date());
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setPatronId("45678915");

        BulkRequestItemEntity savedBulkRequestItemEntity = bulkRequestItemDetailsRepository.save(bulkRequestItemEntity);
        assertNotNull(savedBulkRequestItemEntity);
    }

    @Test
    public void testBulkRequestItemEntity(){
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setId(1);
        bulkRequestItemEntity.setBulkRequestName("TestFirstBulkRequest");
        bulkRequestItemEntity.setBulkRequestFileName("bulkItemUpload");
        bulkRequestItemEntity.setBulkRequestFileData("BARCODE\tCUSTOMER_CODE\n32101075852275\tPK".getBytes());
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setBulkRequestStatus(RecapConstants.PROCESSED);
        bulkRequestItemEntity.setCreatedBy("TestUser");
        bulkRequestItemEntity.setCreatedDate(new Date());
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setPatronId("45678915");
        assertNotNull(bulkRequestItemEntity.getId());
        assertNotNull(bulkRequestItemEntity.getBulkRequestName());
        assertNotNull(bulkRequestItemEntity.getBulkRequestFileName());
        assertNotNull(bulkRequestItemEntity.getBulkRequestFileData());
        assertNotNull(bulkRequestItemEntity.getRequestingInstitutionId());
        assertNotNull(bulkRequestItemEntity.getBulkRequestStatus());
        assertNotNull(bulkRequestItemEntity.getCreatedBy());
        assertNotNull(bulkRequestItemEntity.getCreatedDate());
        assertNotNull(bulkRequestItemEntity.getStopCode());
        assertNotNull(bulkRequestItemEntity.getPatronId());
    }
}
