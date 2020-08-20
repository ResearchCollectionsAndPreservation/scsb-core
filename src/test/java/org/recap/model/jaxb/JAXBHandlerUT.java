package org.recap.model.jaxb;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class JAXBHandlerUT extends BaseTestCase {

    JAXBHandler jaxBHandler;
    @Mock
    Object object;

    @Test
    public void testJAXBHandler() {
        jaxBHandler = JAXBHandler.getInstance();
        String res = jaxBHandler.marshal("test");
        try {
            Object objectValue = jaxBHandler.unmarshal("Test Data", object.getClass());
            assertNotNull(objectValue);
        } catch (JAXBException e) {
        }
        Map<String, Unmarshaller> data = new HashMap<>();
        jaxBHandler.setUnmarshallerMap(data);
        Map<String, Marshaller> marshallerMap = new HashMap<>();
        Map<String, Unmarshaller> map = jaxBHandler.getUnmarshallerMap();
        jaxBHandler.setMarshallerMap(marshallerMap);
        Map<String, Marshaller> mapnew = jaxBHandler.getMarshallerMap();
    }
}

