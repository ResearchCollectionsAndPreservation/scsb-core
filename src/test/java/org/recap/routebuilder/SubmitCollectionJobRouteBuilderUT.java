package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.controller.SubmitCollectionJobController;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Charan Raj C created on 02/11/23
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SubmitCollectionJobRouteBuilderUT {

    @InjectMocks
    SubmitCollectionJobRouteBuilder submitCollectionJobRouteBuilder;

    @Mock
    private SubmitCollectionJobController submitCollectionJobController;

    private CamelContext camelContext;

    @Mock
    Exchange exchange;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        camelContext = new DefaultCamelContext();
    }

    @Test
    public void submitCollectionJobTest(){
        SubmitCollectionJobRouteBuilder submitCollectionJobRouteBuilder = new SubmitCollectionJobRouteBuilder(camelContext, submitCollectionJobController);

    }

    @Test
    public void submitCollectionJobException() throws Exception{
        try{
            Mockito.when(new SubmitCollectionJobRouteBuilder(camelContext, submitCollectionJobController)).thenThrow(RuntimeException.class);}catch (Exception e){
            e.printStackTrace();
        }
    }




}
