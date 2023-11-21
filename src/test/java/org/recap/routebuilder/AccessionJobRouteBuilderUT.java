package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.controller.SharedCollectionRestController;
import org.springframework.context.ApplicationContext;

/**
 * @author Charan Raj C created on 02/11/23
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AccessionJobRouteBuilderUT {

    @InjectMocks
    AccessionJobRouteBuilder routeBuilder;

    private CamelContext camelContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    SharedCollectionRestController sharedCollectionRestController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        camelContext = new DefaultCamelContext();
    }

    @Test
    public void accessionJobRouteTest(){
        try {
            Mockito.when(new AccessionJobRouteBuilder(camelContext, applicationContext,sharedCollectionRestController)).thenThrow(RuntimeException.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
