package org.recap.controllerit;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.ScsbCommonConstants;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class SubmitCollectionJobControllerIT {
    MockMvc mockMvc;

    @Test
    public void teststartSubmitCollection() throws Exception {
        try {
            MvcResult mvcResult = this.mockMvc.perform(post("/submitCollectionJob/startSubmitCollection")
                    ).andExpect(status().isOk())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            assertNotNull(result);
            int status = mvcResult.getResponse().getStatus();
            assertTrue(status == 200);
            assertEquals(ScsbCommonConstants.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
