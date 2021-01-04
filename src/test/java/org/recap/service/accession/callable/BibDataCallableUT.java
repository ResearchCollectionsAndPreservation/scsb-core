package org.recap.service.accession.callable;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.util.AccessionProcessService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;


/**
 * Created by hemalathas on 7/7/17.
 */
public class BibDataCallableUT extends BaseTestCaseUT {

    @InjectMocks
    BibDataCallable bibDataCallable;

    @Mock
    AccessionProcessService accessionProcessService;

    @Test
    public void testBibDataCallaable() throws Exception {
        bibDataCallable.setAccessionRequest(new AccessionRequest());
        bibDataCallable.setOwningInstitution("PUL");
        bibDataCallable.setWriteToReport(true);
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        bibDataCallable.setImsLocationEntity(imsLocationEntity);
        Set<AccessionResponse> accessionResponses=new HashSet<>();
        AccessionResponse accessionResponse=new AccessionResponse();
        accessionResponse.setMessage("test");
        accessionResponse.setItemBarcode("123");
        accessionResponses.add(accessionResponse);
        Mockito.when(accessionProcessService.processRecords(Mockito.anySet(),Mockito.anyList(),Mockito.any(),Mockito.anyList(),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any())).thenReturn(accessionResponses);
        Object object= bibDataCallable.call();
        assertEquals(accessionResponses,object);
    }

}