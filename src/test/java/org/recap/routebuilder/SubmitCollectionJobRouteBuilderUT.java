package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.controller.SubmitCollectionJobController;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Charan Raj C created on 02/11/23
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class SubmitCollectionJobRouteBuilderUT {

    @InjectMocks
    SubmitCollectionJobRouteBuilder submitCollectionJobRouteBuilder;

    @Mock
    private SubmitCollectionJobController submitCollectionJobController;

    private CamelContext camelContext;

    @Mock
    Exchange exchange;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        camelContext = new DefaultCamelContext();
    }

    @Test
    public void submitCollectionJobTest() {
        SubmitCollectionJobRouteBuilder submitCollectionJobRouteBuilder = new SubmitCollectionJobRouteBuilder(camelContext, submitCollectionJobController);

    }

    @Test
    public void submitCollectionJobException() throws Exception {
        try {
            Mockito.when(new SubmitCollectionJobRouteBuilder(camelContext, submitCollectionJobController)).thenThrow(RuntimeException.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
