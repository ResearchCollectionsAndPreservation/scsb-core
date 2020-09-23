package org.recap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;


@TestPropertySource("classpath:application.properties")
@RunWith(MockitoJUnitRunner.class)
public class BaseTestCaseUT {

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void contextLoads() {
    }
}
