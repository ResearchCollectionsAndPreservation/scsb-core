package org.recap.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XmlToBibEntityConverterFactory {

    private final List<AccessionXmlConverterAbstract> accessionXmlConverterAbstractList;

    @Autowired
    public XmlToBibEntityConverterFactory(List<AccessionXmlConverterAbstract> accessionXmlConverterAbstractList) {
        this.accessionXmlConverterAbstractList = accessionXmlConverterAbstractList;
    }

    public AccessionXmlToBibEntityConverterInterface getConverter(String format){
        return accessionXmlConverterAbstractList
                .stream()
                .filter(accessionConvertor -> accessionConvertor.isFormat(format))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
