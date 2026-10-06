package org.recap.model.submitcollection;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BarcodeBibliographicEntityObjectUT {
    @Test
    public void BarcodeBibliographicEntityObject() {
        BarcodeBibliographicEntityObject barcodeBibliographicEntityObject = new BarcodeBibliographicEntityObject();
        barcodeBibliographicEntityObject.setBarcode("1234");
        barcodeBibliographicEntityObject.setOwningInstitutionBibId("1234");
        barcodeBibliographicEntityObject.setBibliographicEntity(null);
        BarcodeBibliographicEntityObject barcodeBibliographicEntityObject1 = new BarcodeBibliographicEntityObject();
        barcodeBibliographicEntityObject.setBarcode(null);
        barcodeBibliographicEntityObject.setOwningInstitutionBibId("1234");
        barcodeBibliographicEntityObject.equals(barcodeBibliographicEntityObject);
        barcodeBibliographicEntityObject.equals(barcodeBibliographicEntityObject1);
        assertNull(barcodeBibliographicEntityObject.getBibliographicEntity());
        assertNotNull(barcodeBibliographicEntityObject.getOwningInstitutionBibId());
        assertNotNull(barcodeBibliographicEntityObject.hashCode());
    }
}
