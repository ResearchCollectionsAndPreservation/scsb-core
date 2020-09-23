package org.recap.model.jpa;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class PendingRequestEntityUT {

    @Test
    public void getPendingRequestEntity(){
        PendingRequestEntity pendingRequestEntity = new PendingRequestEntity();
        pendingRequestEntity.setId(1);
        pendingRequestEntity.setRequestItemEntity(new RequestItemEntity());
        pendingRequestEntity.setRequestId(1);
        pendingRequestEntity.setRequestCreatedDate(new Date());
        pendingRequestEntity.setItemId(1);
        pendingRequestEntity.setItemEntity(new ItemEntity());

        assertNotNull(pendingRequestEntity.getId());
        assertNotNull(pendingRequestEntity.getRequestItemEntity());
        assertNotNull(pendingRequestEntity.getRequestId());
        assertNotNull(pendingRequestEntity.getRequestCreatedDate());
        assertNotNull(pendingRequestEntity.getItemId());
        assertNotNull(pendingRequestEntity.getItemEntity());
    }
}
