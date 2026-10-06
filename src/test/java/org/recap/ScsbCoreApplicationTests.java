package org.recap;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(classes = ScsbCoreApplication.class)
public class ScsbCoreApplicationTests {

    @Test
    public void contextLoads() {
    }

}
