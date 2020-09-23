package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.ils.model.BibRecord;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class BibRecordsUT {

    @Test
    public  void getBibRecords(){
        BibRecords bibRecords = new BibRecords();
        bibRecords.setBibRecords(Arrays.asList(new BibRecord()));
        assertNotNull(bibRecords.getBibRecords());
        assertNotNull(bibRecords);
    }
}
