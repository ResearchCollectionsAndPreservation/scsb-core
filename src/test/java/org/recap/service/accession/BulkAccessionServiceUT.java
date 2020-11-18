package org.recap.service.accession;

import org.apache.camel.Exchange;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.accession.AccessionSummary;
import org.recap.model.jpa.AccessionEntity;
import org.recap.repository.jpa.AccessionDetailsRepository;
import org.recap.service.accession.callable.BibDataCallable;
import org.recap.util.AccessionProcessService;
import org.recap.util.AccessionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class BulkAccessionServiceUT extends BaseTestCaseUT {

    @InjectMocks
    BulkAccessionService bulkAccessionService;

    @Mock
    Exchange exchange;


    @Mock
    AccessionProcessService accessionProcessService;

    @Mock
    AccessionValidationService accessionValidationService;

    @Mock
    AccessionDetailsRepository accessionDetailsRepository;

    @Mock
    AccessionValidationService.AccessionValidationResponse accessionValidationResponse;

    @Mock
    AccessionUtil accessionUtil;

    @Mock
    ApplicationContext applicationContext;

    @Value("${batch.accession.thread.size}")
    int batchAccessionThreadSize;

    @Ignore
    public void doBulkAccession() {
        List<AccessionRequest> accessionRequestList=getAccessionRequests();
        AccessionSummary accessionSummary=new AccessionSummary("test");
        Mockito.when(accessionProcessService.removeDuplicateRecord(Mockito.anyList())).thenReturn(removeDuplicateRecord(accessionRequestList));
        ReflectionTestUtils.setField(bulkAccessionService,"batchAccessionThreadSize",batchAccessionThreadSize);
        Mockito.when(accessionValidationService.validateBarcodeOrCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(accessionValidationResponse);
        Mockito.when(accessionValidationResponse.getOwningInstitution()).thenReturn("PUL");
        Mockito.when(accessionValidationResponse.getMessage()).thenReturn(RecapConstants.ITEM_ALREADY_ACCESSIONED);
        Mockito.when(accessionValidationResponse.isValid()).thenReturn(true);
        BibDataCallable bibDataCallable = Mockito.mock(BibDataCallable.class);
        Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(bibDataCallable);
        List<AccessionResponse> response=bulkAccessionService.doAccession(accessionRequestList,accessionSummary,exchange);

    }

    @Test
    public void doBulkAccessionInvalid() {
        List<AccessionRequest> accessionRequestList=getAccessionRequests();
        AccessionSummary accessionSummary=new AccessionSummary("test");
        Mockito.when(accessionProcessService.removeDuplicateRecord(Mockito.anyList())).thenReturn(removeDuplicateRecord(accessionRequestList));
        ReflectionTestUtils.setField(bulkAccessionService,"batchAccessionThreadSize",batchAccessionThreadSize);
        Mockito.when(accessionValidationService.validateBarcodeOrCustomerCode(Mockito.anyString(),Mockito.anyString())).thenReturn(accessionValidationResponse);
        Mockito.when(accessionValidationResponse.getOwningInstitution()).thenReturn("PUL");
        Mockito.when(accessionValidationResponse.getMessage()).thenReturn(RecapConstants.ITEM_ALREADY_ACCESSIONED);
        List<AccessionResponse> response=bulkAccessionService.doAccession(accessionRequestList,accessionSummary,exchange);
        assertNull(response);
    }

    @Test
    public void saveRequest() {
        List<AccessionRequest> accessionRequestList=getAccessionRequests();
        String status=bulkAccessionService.saveRequest(accessionRequestList);
        assertEquals(RecapConstants.ACCESSION_SAVE_SUCCESS_STATUS,status);
    }

    @Test
    public void saveRequestException() {
        List<AccessionRequest> accessionRequestList=getAccessionRequests();
        Mockito.when(accessionDetailsRepository.save(Mockito.any())).thenThrow(NullPointerException.class);
        String status=bulkAccessionService.saveRequest(accessionRequestList);
        assertTrue(status.contains(RecapConstants.ACCESSION_SAVE_FAILURE_STATUS));
    }

    @Test
    public void getAccessionRequestException() {
        Mockito.when(accessionDetailsRepository.findByAccessionStatus(Mockito.anyString())).thenReturn(getAccessionEntities("pending"));
        List<AccessionEntity> accessionEntities=bulkAccessionService.getAccessionEntities(RecapConstants.PENDING);
        List<AccessionRequest> accessionEntity1=bulkAccessionService.getAccessionRequest(accessionEntities);
        assertNotNull(accessionEntity1);
        assertEquals(java.util.Optional.ofNullable(1), java.util.Optional.ofNullable(accessionEntities.get(0).getId()));
    }

    @Test
    public void getAccessionRequest() {
        Mockito.when(accessionDetailsRepository.findByAccessionStatus(Mockito.anyString())).thenReturn(getAccessionEntities("pending"));
        bulkAccessionService.updateStatusForAccessionEntities(getAccessionEntities("[{\"customerCode\":\"PA\",\"itemBarcode\":\"123\"}]"),RecapConstants.PENDING);
        List<AccessionRequest> accessionEntity1=bulkAccessionService.getAccessionRequest(getAccessionEntities("[{\"customerCode\":\"PA\",\"itemBarcode\":\"123\"}]"));
        assertNotNull(accessionEntity1);
   }

    private List<AccessionEntity> getAccessionEntities(String request) {
        List<AccessionEntity> accessionEntityList=new ArrayList<>();
        AccessionEntity accessionEntity=new AccessionEntity();
        accessionEntity.setAccessionRequest(request);
        accessionEntity.setAccessionStatus(RecapConstants.PENDING);
        accessionEntity.setCreatedDate(new Date());
        accessionEntity.setId(1);
        accessionEntityList.add(accessionEntity);
        return accessionEntityList;
    }

    public List<AccessionRequest> removeDuplicateRecord(List<AccessionRequest> trimmedAccessionRequests) {
        Set<AccessionRequest> accessionRequests = new HashSet<>(trimmedAccessionRequests);
        return new ArrayList<>(accessionRequests);
    }

    private List<AccessionRequest> getAccessionRequests() {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        accessionRequestList.add(accessionRequest);
        return accessionRequestList;
    }
}
