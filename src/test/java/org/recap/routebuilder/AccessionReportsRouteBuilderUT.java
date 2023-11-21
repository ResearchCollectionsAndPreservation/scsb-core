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

/**
 * @author Charan Raj C created on 02/11/23
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AccessionReportsRouteBuilderUT  {

    @Mock
    private ReportProcessor reportProcessor;

    @InjectMocks
    AccessionReportsRouteBuilder accessionReportsRouteBuilder;

    private CamelContext camelContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        camelContext = new DefaultCamelContext();
    }

    @Test
    public void accessionReportsTest(){
        AccessionReportsRouteBuilder routeBuilder = new AccessionReportsRouteBuilder(camelContext,reportProcessor);
    }

    @Test
    public void accessionReportsRouteBuilderException()throws Exception {
        try{
            Mockito.when(new AccessionReportsRouteBuilder(camelContext, reportProcessor)).thenThrow(RuntimeException.class);}catch (Exception e){
            e.printStackTrace();
        }
    }


}
