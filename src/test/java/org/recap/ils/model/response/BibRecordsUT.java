package org.recap.ils.model.response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.BibRecord;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class BibRecordsUT extends BaseTestCaseUT {

    @InjectMocks
    BibRecords bibRecords;

    List<BibRecord> bibRecordsList=new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(bibRecords,"bibRecords",bibRecordsList);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getBibRecords() throws Exception {
        bibRecords.setBibRecords(bibRecordsList);
        List<BibRecord> bib=bibRecords.getBibRecords();
        assertNotNull(bib);
    }
}
