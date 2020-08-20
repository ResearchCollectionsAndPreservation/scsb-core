package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.DeliveryRestrictionEntity;
import org.recap.model.jpa.InstitutionEntity;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 13/7/17.
 */
public class DeliveryRestrictionEntityUT extends BaseTestCase{

    @Test
    public void testDeliveryRestrictionEntity(){
        DeliveryRestrictionEntity deliveryRestrictionEntity = new DeliveryRestrictionEntity();
        deliveryRestrictionEntity.setId(1);
        deliveryRestrictionEntity.setDeliveryRestriction("Test");
        deliveryRestrictionEntity.setInstitutionEntity(new InstitutionEntity());
        deliveryRestrictionEntity.setCustomerCodeEntityList(Arrays.asList(new CustomerCodeEntity()));

        assertNotNull(deliveryRestrictionEntity.getCustomerCodeEntityList());
        assertNotNull(deliveryRestrictionEntity.getInstitutionEntity());
        assertNotNull(deliveryRestrictionEntity.getDeliveryRestriction());
        assertNotNull(deliveryRestrictionEntity.getId());
    }

}