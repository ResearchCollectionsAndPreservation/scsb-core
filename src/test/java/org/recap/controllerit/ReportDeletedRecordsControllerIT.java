package org.recap.controllerit;

import org.junit.jupiter.api.Test;
import org.recap.ScsbConstants;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportDeletedRecordsControllerIT extends BaseControllerUT {


    @Test
    public void testDeletedRecords() throws Exception {
        //  RequestItemEntity requestItemEntity=createRequestItem();
        MvcResult mvcResult = this.mockMvc.perform(get("/reportDeleted/records")
                ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        assertTrue(result.contains(ScsbConstants.DELETED_RECORDS_SUCCESS_MSG));
    }

}
