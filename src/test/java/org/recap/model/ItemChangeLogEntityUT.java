package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 17/2/17.
 */
public class ItemChangeLogEntityUT extends BaseTestCase{

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Test
    public void testItemChangeLogEntity(){
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setNotes("test");
        itemChangeLogEntity.setOperationType("test");
        itemChangeLogEntity.setUpdatedBy("test");
        itemChangeLogEntity.setRecordId(1);
        itemChangeLogEntity.setId(12);
        itemChangeLogEntity.setUpdatedDate(new Date());
        ItemChangeLogEntity savedItemChangeLogEntity = itemChangeLogDetailsRepository.save(itemChangeLogEntity);
        assertNotNull(savedItemChangeLogEntity);
        assertNotNull(savedItemChangeLogEntity.getId());
        assertNotNull(savedItemChangeLogEntity.getNotes());
        assertNotNull(savedItemChangeLogEntity.getOperationType());
        assertNotNull(savedItemChangeLogEntity.getUpdatedBy());
        assertNotNull(savedItemChangeLogEntity.getUpdatedDate());
        assertNotNull(savedItemChangeLogEntity.getId());
    }


}