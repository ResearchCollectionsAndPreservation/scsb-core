package org.recap.controllerit;

import org.junit.jupiter.api.Test;
import org.recap.ScsbCommonConstants;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusReconciliationControllerIT extends BaseControllerUT {

    @Test
    public void testitemStatusReconciliation() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/statusReconciliation/itemStatusReconciliation")
                ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        assertEquals(ScsbCommonConstants.SUCCESS, result);
    }
}
