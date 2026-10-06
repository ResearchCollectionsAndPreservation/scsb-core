package org.recap.service.accession;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class BibDataFactoryUT extends BaseTestCaseUT {

    @InjectMocks
    BibDataFactory bibDataFactory;

    @Test
    public void getConverter() {
        List<BibDataAbstract> bibDataAbstractList = new ArrayList<>();
        ReflectionTestUtils.setField(bibDataFactory, "bibDataAbstractList", bibDataAbstractList);
        try {
            bibDataFactory.getAuth("test");
        } catch (Exception e) {
        }
    }
}
