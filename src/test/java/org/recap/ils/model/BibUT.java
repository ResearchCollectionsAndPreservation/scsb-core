package org.recap.ils.model;

import org.junit.Test;


import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class BibUT {

    @Test
    public void getBib(){
        Bib bib = new Bib();
        ContentType contentType = new ContentType();
        CollectionType collectionType = new CollectionType();
        RecordType recordType = new RecordType();
        recordType.setType(RecordTypeType.BIBLIOGRAPHIC);
        collectionType.setId("1");
        collectionType.setRecord(Arrays.asList(recordType));
        assertNotNull(collectionType.getId());
        assertNotNull(collectionType.getRecord());
        contentType.setCollection(collectionType);
        assertNotNull(contentType.getCollection());
        bib.setContent(contentType);
        bib.setOwningInstitutionBibId("23445");
        bib.setOwningInstitutionId("1");
        assertNotNull(bib.getContent());
        assertNotNull(bib.getOwningInstitutionBibId());
        assertNotNull(bib.getOwningInstitutionId());
    }
}
