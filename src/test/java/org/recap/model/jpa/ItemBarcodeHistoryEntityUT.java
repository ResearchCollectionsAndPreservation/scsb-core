package org.recap.model.jpa;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ItemBarcodeHistoryEntityUT {

    @Test
    public void getItemBarcodeHistoryEntity(){
        ItemBarcodeHistoryEntity itemBarcodeHistoryEntity = new ItemBarcodeHistoryEntity();
        itemBarcodeHistoryEntity.setCreatedDate(new Date());
        itemBarcodeHistoryEntity.setNewBarcode("23567");
        itemBarcodeHistoryEntity.setOldBarcode("255678");
        itemBarcodeHistoryEntity.setOwningingInstitution("PUL");
        itemBarcodeHistoryEntity.setOwningingInstitutionItemId("1");
        assertNotNull(itemBarcodeHistoryEntity.getCreatedDate());
        assertNotNull(itemBarcodeHistoryEntity.getNewBarcode());
        assertNotNull(itemBarcodeHistoryEntity.getOldBarcode());
        assertNotNull(itemBarcodeHistoryEntity.getOwningingInstitution());
        assertNotNull(itemBarcodeHistoryEntity.getOwningingInstitutionItemId());
    }
}
