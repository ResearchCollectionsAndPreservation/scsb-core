package org.recap.converter;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XmlToBibEntityConverterFactory {

    private final BeanFactory beanFactory;

    @Autowired
    public XmlToBibEntityConverterFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public AccessionXmlToBibEntityConverterInterface getConverter(String instituion){
        if(instituion.equalsIgnoreCase("PUL") || instituion.equalsIgnoreCase("CUL")){
            return beanFactory.getBean(AccessionMarcToBibEntityConverter.class);
        }
        else {
            return beanFactory.getBean(AccessionSCSBToBibEntityConverter.class);
        }
    }
}
