package org.recap.converter;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class XmlToBibEntityConverterFactoryUT extends BaseTestCaseUT {

    @InjectMocks
    XmlToBibEntityConverterFactory xmlToBibEntityConverterFactory;

    @Test
    public void getConverter() {
        List<AccessionXmlConverterAbstract> accessionXmlConverterAbstractList = new ArrayList<>();
        ReflectionTestUtils.setField(xmlToBibEntityConverterFactory, "accessionXmlConverterAbstractList", accessionXmlConverterAbstractList);
        try {
            xmlToBibEntityConverterFactory.getConverter("test");
        } catch (Exception e) {
        }
    }
}
