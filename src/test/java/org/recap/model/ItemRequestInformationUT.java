package org.recap.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.ItemRequestInformation;


import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemRequestInformationUT extends BaseTestCaseUT {

    @Test
    public void testItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestingInstitution("PUL");
        assertTrue(itemRequestInformation.isOwningInstitutionItem());
    }
}
