package org.recap.ils.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecordTypeTypeUT extends BaseTestCaseUT {


    @Test
    public void testfromValue() {
        RecordTypeType.fromValue("Bibliographic");
        assertTrue(true);
    }
}
