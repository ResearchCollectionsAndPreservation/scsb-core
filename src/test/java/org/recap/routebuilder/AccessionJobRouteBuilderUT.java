package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.controller.SharedCollectionRestController;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Charan Raj C created on 02/11/23
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class AccessionJobRouteBuilderUT {

    private CamelContext camelContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    SharedCollectionRestController sharedCollectionRestController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        camelContext = new DefaultCamelContext();
    }

    @Test
    public void accessionJobRouteTest() {
        new AccessionJobRouteBuilder(camelContext, applicationContext, sharedCollectionRestController);
    }
}
