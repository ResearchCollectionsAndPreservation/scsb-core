package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Charan Raj C created on 02/11/23
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class AccessionReportsRouteBuilderUT {

    @Mock
    private ReportProcessor reportProcessor;

    @InjectMocks
    AccessionReportsRouteBuilder accessionReportsRouteBuilder;

    private CamelContext camelContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        camelContext = new DefaultCamelContext();
    }

    @Test
    public void accessionReportsTest() {
        AccessionReportsRouteBuilder routeBuilder = new AccessionReportsRouteBuilder(camelContext, reportProcessor);
    }

    @Test
    public void accessionReportsRouteBuilderException() throws Exception {
        try {
            Mockito.when(new AccessionReportsRouteBuilder(camelContext, reportProcessor)).thenThrow(RuntimeException.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
