package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.record.Record;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.ils.model.BibRecord;
import org.recap.model.marc.BibMarcRecord;

import java.util.List;

import static org.junit.Assert.assertNull;

public class MarcUtilUT {
    @Mock
    MarcUtil mockMarcUtil;
    @Before
    public void beforeSetuo(){
        mockMarcUtil= Mockito.mock(MarcUtil.class);
    }
    @Test
    public void testgetSecondIndicatorForDataField(){
        Record response = null;
        Mockito.when(mockMarcUtil.readMarcXml("TestData ")).thenReturn(null);
        mockMarcUtil.readMarcXml("testdata");
        assertNull(response);
    }
}
