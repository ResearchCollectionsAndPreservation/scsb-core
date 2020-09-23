package org.recap.model.jpa;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 14/3/17.
 */
public class CustomerCodeEntityUT {

    @Test
    public void testCustomerCode(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        assertNotNull(institutionEntity);

        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setId(1);
        customerCodeEntity.setCustomerCode("AB");
        customerCodeEntity.setDeliveryRestrictions("AC,BC");
        customerCodeEntity.setRecapDeliveryRestrictions("No Restriction");
        customerCodeEntity.setPwdDeliveryRestrictions("Others");
        customerCodeEntity.setDescription("test");
        customerCodeEntity.setOwningInstitutionId(institutionEntity.getId());
        customerCodeEntity.setInstitutionEntity(institutionEntity);
        customerCodeEntity.setPickupLocation("Discovery");
        customerCodeEntity.setDeliveryRestrictionEntityList(Arrays.asList(new DeliveryRestrictionEntity()));
        customerCodeEntity.equals(customerCodeEntity);

        assertNotNull(customerCodeEntity.getId());
        assertEquals(customerCodeEntity.getCustomerCode(),"AB");
        assertEquals(customerCodeEntity.getDeliveryRestrictions(),"AC,BC");
        assertEquals(customerCodeEntity.getDescription(),"test");
        assertEquals(customerCodeEntity.getPickupLocation(),"Discovery");
        assertNotNull(customerCodeEntity.getOwningInstitutionId());
        assertNotNull(customerCodeEntity.getInstitutionEntity());
        assertNotNull(customerCodeEntity.getRecapDeliveryRestrictions());
        assertNotNull(customerCodeEntity.getPwdDeliveryRestrictions());
        assertNotNull(customerCodeEntity.getDeliveryRestrictionEntityList());

        CustomerCodeEntity customerCodeEntity1 = new CustomerCodeEntity();
        customerCodeEntity1.setId(2);
        customerCodeEntity1.setDescription("PA");
        customerCodeEntity1.setCustomerCode("OP");
        CustomerCodeEntity customerCodeEntity2 = new CustomerCodeEntity();
        customerCodeEntity1.setId(2);
        customerCodeEntity1.setDescription("PA");
        customerCodeEntity1.setCustomerCode("OP");
        customerCodeEntity1.compareTo(customerCodeEntity);
        customerCodeEntity1.equals(customerCodeEntity);
        customerCodeEntity.hashCode();
        customerCodeEntity1.compareTo(customerCodeEntity2);
        customerCodeEntity1.equals(customerCodeEntity2);
    }

}