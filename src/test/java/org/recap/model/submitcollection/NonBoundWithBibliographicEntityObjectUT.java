package org.recap.model.submitcollection;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NonBoundWithBibliographicEntityObjectUT extends BaseTestCase {


    @Test
    public void testNonBoundWithBibliographicEntityObject() {
        Random random = new Random();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = new NonBoundWithBibliographicEntityObject();
        nonBoundWithBibliographicEntityObject.setBibliographicEntityList(null);
        nonBoundWithBibliographicEntityObject.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        assertNull(nonBoundWithBibliographicEntityObject.getBibliographicEntityList());
        assertNotNull(nonBoundWithBibliographicEntityObject.getOwningInstitutionBibId());
    }
}
