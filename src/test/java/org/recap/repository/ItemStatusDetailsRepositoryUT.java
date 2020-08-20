package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 17/11/16.
 */
public class ItemStatusDetailsRepositoryUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(ItemStatusDetailsRepositoryUT.class);
    @Autowired
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Test
    public void testItemStatus(){
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findById(1).orElse(null);
        logger.info(itemStatusEntity.getStatusCode());
        assertNotNull(itemStatusEntity);
        assertEquals(itemStatusEntity.getStatusCode(),"Available");
    }



}