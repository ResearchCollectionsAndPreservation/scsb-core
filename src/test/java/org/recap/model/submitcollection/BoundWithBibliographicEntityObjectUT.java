package org.recap.model.submitcollection;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BoundWithBibliographicEntityObjectUT extends BaseTestCase {
    @Test
    public void testBoundWithBibliographicEntityObject() {
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = new BoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObject.setBarcode("1234");
        boundWithBibliographicEntityObject.setBibliographicEntityList(null);
        assertNull(boundWithBibliographicEntityObject.getBibliographicEntityList());
        assertNotNull(boundWithBibliographicEntityObject.getBarcode());
    }
}
