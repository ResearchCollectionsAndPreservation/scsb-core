package org.recap.controllerit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbCommonConstants;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SubmitCollectionJobControllerIT {
    MockMvc mockMvc;
    @Test
    public void teststartSubmitCollection() throws Exception{
        try {
            MvcResult mvcResult = this.mockMvc.perform(post("/submitCollectionJob/startSubmitCollection")
                    ).andExpect(status().isOk())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            assertNotNull(result);
            int status = mvcResult.getResponse().getStatus();
            assertTrue(status == 200);
            assertEquals(ScsbCommonConstants.SUCCESS, result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
