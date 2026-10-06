package org.recap.camel.submitcollection;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.recap.model.ILSConfigProperties;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class SubmitCollectionPollingS3RouteBuilderUT {

    @InjectMocks
    SubmitCollectionPollingS3RouteBuilder submitCollectionPollingS3RouteBuilder;

    @Mock
    CommonUtil commonUtil;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    org.springframework.context.ApplicationContext applicationContext;

    @Mock
    org.recap.camel.submitcollection.processor.SubmitCollectionProcessor submitCollectionProcessor;

    @Mock
    CamelContext camelContext;

    @Mock
    AmazonS3 awsS3Client;

    @Mock
    S3Object s3Object;

    @Mock
    S3ObjectSummary s3ObjectSummary;

    @Mock
    S3ObjectInputStream inputStream;

    @Mock
    ObjectListing objectListing;

    @Mock
    RouteController routeController;

    @Mock
    Exchange exchange;

    @Mock
    ProducerTemplate producer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.lenient().when(applicationContext.getBean(org.recap.camel.submitcollection.processor.SubmitCollectionProcessor.class)).thenReturn(submitCollectionProcessor);
        Mockito.lenient().when(applicationContext.getBean(Mockito.eq(org.recap.camel.submitcollection.processor.SubmitCollectionProcessor.class), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(submitCollectionProcessor);
    }

    @Test
    public void createRoutesForSubmitCollection() throws Exception {
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(getStrings());
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setBibDataFormat("test");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        submitCollectionPollingS3RouteBuilder.createRoutesForSubmitCollection();
    }

    @Test
    public void startNextRouteInNewThreadException() throws Exception {
        submitCollectionPollingS3RouteBuilder.startNextRouteInNewThread(exchange, "1");
    }

    @Test
    public void startNextRouteInNewThread() throws Exception {
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        submitCollectionPollingS3RouteBuilder.startNextRouteInNewThread(exchange, "1");
    }

    @Test
    public void startNextRouteInNewThreadComplete() throws Exception {
        submitCollectionPollingS3RouteBuilder.startNextRouteInNewThread(exchange, "1Complete");

    }

    @Test
    public void addRoutesToCamelContext() {
        String scsbBucketName = "test";
        String fileName = "test";
        String currentInstitution = "PUL";
        String cgdType = "Shared";
        String currentInstitutionRouteId = "1";
        String nextInstitutionRouteId = "1";
        Boolean isCGDProtected = true;
        ReflectionTestUtils.setField(submitCollectionPollingS3RouteBuilder, "submitCollectionLocalWorkingDir", "resources/org/recap/service/");
        ReflectionTestUtils.setField(submitCollectionPollingS3RouteBuilder, "submitCollectionS3BasePath", "test");
        ReflectionTestUtils.setField(submitCollectionPollingS3RouteBuilder, "scsbBucketName", scsbBucketName);
        Mockito.when(awsS3Client.listObjects(anyString())).thenReturn(objectListing);
        Mockito.when(objectListing.getObjectSummaries()).thenReturn(Arrays.asList(s3ObjectSummary));
        Mockito.when(s3ObjectSummary.getKey()).thenReturn("testKey");
        Mockito.when(awsS3Client.getObject(scsbBucketName, fileName)).thenReturn(s3Object);
        Mockito.when(s3Object.getObjectContent()).thenReturn(inputStream);
        submitCollectionPollingS3RouteBuilder.addRoutesToCamelContext(currentInstitution, cgdType, currentInstitutionRouteId, nextInstitutionRouteId, isCGDProtected);
    }

    @Test
    public void removeRoutesForSubmitCollection() throws Exception {
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(getStrings());
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        submitCollectionPollingS3RouteBuilder.removeRoutesForSubmitCollection();
    }

    @Test
    public void getObjectContentToDrive() {
        String fileName = "test";
        String currentInstitution = "PUL";
        String cgdType = "Shared";
        String scsbBucketName = "test";
        ReflectionTestUtils.setField(submitCollectionPollingS3RouteBuilder, "submitCollectionLocalWorkingDir", "resources/org/recap/service/");
        ReflectionTestUtils.setField(submitCollectionPollingS3RouteBuilder, "scsbBucketName", scsbBucketName);
        Mockito.when(awsS3Client.getObject(scsbBucketName, fileName)).thenReturn(s3Object);
        Mockito.when(s3Object.getObjectContent()).thenReturn(inputStream);
        ReflectionTestUtils.invokeMethod(submitCollectionPollingS3RouteBuilder, "getObjectContentToDrive", fileName, currentInstitution, cgdType);
    }

    @Test
    public void clearDirectory() {
        String institutionCode = "PUL";
        String cgdType = "Shared";
        ReflectionTestUtils.setField(submitCollectionPollingS3RouteBuilder, "submitCollectionLocalWorkingDir", "resources/org/recap/service/");
        try {
            submitCollectionPollingS3RouteBuilder.clearDirectory(institutionCode, cgdType);
        } catch (Exception e) {
        }
    }


    private List<String> getStrings() {
        List<String> allInstitutionCodeExceptSupportInstitution = new ArrayList<>();
        allInstitutionCodeExceptSupportInstitution.add("PUL");
        allInstitutionCodeExceptSupportInstitution.add("CUL");
        allInstitutionCodeExceptSupportInstitution.add("NYPL");
        return allInstitutionCodeExceptSupportInstitution;
    }
}
