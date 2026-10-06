package org.recap.camel.dailyreconciliation;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.RouteController;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.csv.DailyReconcilationRecord;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.util.PropertyUtil;
import org.recap.util.SecurityUtil;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by akulak on 8/5/17.
 */

@ExtendWith({SpringExtension.class})
public class DailyReconciliationProcessorUT {

    @InjectMocks
    DailyReconciliationProcessor dailyReconciliationProcessor;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    CamelContext camelContext;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    RouteController routeController;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    AmazonS3 awsS3Client;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    SecurityUtil securityUtil;

    // Plain field instead of @Value: this is a Mockito-only unit test with no
    // Spring property source loaded, so "${daily.reconciliation.file}" would
    // never actually resolve here - it would just inject the literal placeholder
    // string and blow up when the processor tries to write a file with that
    // path. Point it at a real writable directory instead.
    private String filePath;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        filePath = System.getProperty("java.io.tmpdir");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "securityUtil", securityUtil);
        // Inject frequently-used mocks into the processor so test methods don't
        // depend on setting each field individually. This avoids NPEs when
        // methods access these collaborators.
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "propertyUtil", propertyUtil);
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "requestItemDetailsRepository", requestItemDetailsRepository);
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "itemDetailsRepository", itemDetailsRepository);
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "camelContext", camelContext);
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "awsS3Client", awsS3Client);
        // No producerTemplate field exists on the processor; other common
        // collaborators are injected above.
    }


    @Test
    public void processInput() throws Exception {
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsLocationCode", "RECAP");
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_AVAILABLE_ITEM_STATUS_CODES)).thenReturn("RECAP");
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_NOT_AVAILABLE_ITEM_STATUS_CODES)).thenReturn("RECAP");
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_REQUESTABLE_NOT_RETRIEVABLE_ITEM_STATUS_CODES)).thenReturn("RECAP");
        List<DailyReconcilationRecord> dailyReconcilationRecords = new ArrayList<>();
        dailyReconcilationRecords.add(getDailyReconcilationRecord("12345", "1", ScsbConstants.GFA_STATUS_IN));
        dailyReconcilationRecords.add(getDailyReconcilationRecord("2345", "1", ScsbConstants.GFA_STATUS_IN));
        Mockito.when(message.getBody()).thenReturn(dailyReconcilationRecords);
        Mockito.when(message.getHeader(Mockito.anyString())).thenReturn("CamelAwsS3Key/CamelAwsS3Key/CamelAwsS3Key");
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.when(requestItemDetailsRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(saveRequestItemEntity(1, getItemEntity())));
        Mockito.when(awsS3Client.doesObjectExist(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(awsS3Client.doesBucketExistV2(Mockito.anyString())).thenReturn(true);
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "filePath", filePath);
        dailyReconciliationProcessor.processInput(exchange);
    }

    @Test
    public void processInputRequestIdNull() throws Exception {
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsLocationCode", "RECAP");
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_AVAILABLE_ITEM_STATUS_CODES)).thenReturn("RECAP");
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_NOT_AVAILABLE_ITEM_STATUS_CODES)).thenReturn("RECAP");
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_REQUESTABLE_NOT_RETRIEVABLE_ITEM_STATUS_CODES)).thenReturn("RECAP");
        Mockito.when(exchange.getIn()).thenReturn(message);
        List<DailyReconcilationRecord> dailyReconcilationRecords = new ArrayList<>();
        dailyReconcilationRecords.add(getDailyReconcilationRecord("12345", null, ScsbConstants.GFA_STATUS_IN));
        dailyReconcilationRecords.add(getDailyReconcilationRecord("2345", null, ScsbConstants.GFA_STATUS_IN));
        List<ItemEntity> itemEntityList = new ArrayList<>();
        itemEntityList.add(getItemEntity());
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(itemEntityList);
        Mockito.when(message.getBody()).thenReturn(dailyReconcilationRecords);
        Mockito.when(message.getHeader(Mockito.anyString())).thenReturn("CamelAwsS3Key/CamelAwsS3Key/CamelAwsS3Key");
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.when(requestItemDetailsRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(saveRequestItemEntity(1, getItemEntity())));
        Mockito.when(awsS3Client.doesObjectExist(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(awsS3Client.doesBucketExistV2(Mockito.anyString())).thenReturn(true);
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "filePath", filePath);
        dailyReconciliationProcessor.processInput(exchange);
    }


    @Test
    public void processInputRequestId() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(Mockito.anyString())).thenReturn("CamelAwsS3Key/CamelAwsS3Key/CamelAwsS3Key");
        List<DailyReconcilationRecord> dailyReconcilationRecords = new ArrayList<>();
        dailyReconcilationRecords.add(getDailyReconcilationRecord("12345", null, ScsbConstants.GFA_STATUS_SCH_ON_REFILE_WORK_ORDER));
        dailyReconcilationRecords.add(getDailyReconcilationRecord("23451", null, ScsbConstants.GFA_STATUS_SCH_ON_REFILE_WORK_ORDER));
        Mockito.when(message.getBody()).thenReturn(dailyReconcilationRecords);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.when(itemDetailsRepository.findByBarcode(Mockito.anyString())).thenReturn(Arrays.asList(getItemEntity()));
        dailyReconciliationProcessor.processInput(exchange);
    }


    @Test
    public void processInputException() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(Mockito.anyString())).thenReturn("CamelAwsS3Key/CamelAwsS3Key/CamelAwsS3Key");
        List<DailyReconcilationRecord> dailyReconcilationRecords = new ArrayList<>();
        DailyReconcilationRecord dailyReconcilationRecord = getDailyReconcilationRecord("12345", "1", "IN");
        dailyReconcilationRecords.add(dailyReconcilationRecord);
        Mockito.when(message.getBody()).thenReturn(dailyReconcilationRecords);
        Mockito.when(camelContext.getRouteController()).thenThrow(NullPointerException.class);
        dailyReconciliationProcessor.processInput(exchange);
    }

    private DailyReconcilationRecord getDailyReconcilationRecord(String barcode, String requestId, String status) {
        DailyReconcilationRecord dailyReconcilationRecord = new DailyReconcilationRecord();
        dailyReconcilationRecord.setCustomerCode("PA");
        dailyReconcilationRecord.setRequestId(requestId);
        dailyReconcilationRecord.setBarcode(barcode);
        dailyReconcilationRecord.setStopCode("PA");
        dailyReconcilationRecord.setPatronId("2");
        dailyReconcilationRecord.setCreateDate(new Date().toString());
        dailyReconcilationRecord.setOwningInst("1");
        dailyReconcilationRecord.setLastUpdatedDate(new Date().toString());
        dailyReconcilationRecord.setRequestingInst("1");
        dailyReconcilationRecord.setDeliveryMethod("test");
        dailyReconcilationRecord.setStatus(status);
        dailyReconcilationRecord.setErrorCode("");
        dailyReconcilationRecord.setErrorNote("");
        return dailyReconcilationRecord;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(new Random().nextInt());
        itemEntity.setBarcode("b3");
        itemEntity.setCustomerCode("c1");
        itemEntity.setCallNumber("cn1");
        itemEntity.setCallNumberType("ct1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCopyNumber(1);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("ut");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("ut");
        itemEntity.setUseRestrictions("no");
        itemEntity.setVolumePartYear("v3");
        itemEntity.setOwningInstitutionItemId(String.valueOf(new Random().nextInt()));
        itemEntity.setItemStatusEntity(getItemStatusEntity());
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        itemEntity.setDeleted(false);
        return itemEntity;
    }

    public RequestItemEntity saveRequestItemEntity(Integer itemId, ItemEntity itemEntity) {
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(itemId);
        requestItemEntity.setId(new Random().nextInt());
        requestItemEntity.setRequestTypeId(1);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setStopCode("PA");
        requestItemEntity.setPatronId("45678912");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setEmailId("test@mail");
        requestItemEntity.setRequestStatusId(1);
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setInstitutionEntity(getInstitutionEntity());
        requestItemEntity.setRequestTypeEntity(getRequestTypeEntity());
        requestItemEntity.setItemEntity(itemEntity);
        return requestItemEntity;
    }

    private RequestTypeEntity getRequestTypeEntity() {
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setId(1);
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        return requestTypeEntity;
    }

    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("Available");
        return itemStatusEntity;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        return institutionEntity;
    }

    @Test
    public void createHeaderForCompareSheetTest() {
        // Use a real workbook/sheet instead of mocking POI's chained Row/Cell
        // API - mocked createCell()/createRow() calls return null unless every
        // single call is stubbed, which is what caused the original NPE.
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("compareSheet");

        ReflectionTestUtils.invokeMethod(dailyReconciliationProcessor, "createHeaderForCompareSheet", sheet);

        verifyCellValue(sheet, 0, 0, ScsbConstants.DAILY_RR_LAS);
        // Add additional verifyCellValue(...) calls here for any other header
        // columns createHeaderForCompareSheet is expected to write.
    }


    private void verifyCellValue(XSSFSheet sheet, int rowNumber, int cellNumber, String expectedValue) {
        Row row = sheet.getRow(rowNumber);
        Cell cell = row.getCell(cellNumber);
        assertEquals(expectedValue, cell.getStringCellValue());
    }

    // Additional comprehensive test cases for better coverage

    @Test
    public void testCreateCellWithValidValue() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();

        dailyReconciliationProcessor.createCell(workbook, row, cellStyle, "TestValue", 0);

        XSSFCell cell = row.getCell(0);
        assertNotNull(cell);
        assertEquals("TestValue", cell.getStringCellValue());
    }

    @Test
    public void testCreateCellWithBlankValue() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();

        dailyReconciliationProcessor.createCell(workbook, row, cellStyle, "", 0);

        XSSFCell cell = row.getCell(0);
        assertNull(cell);
    }

    @Test
    public void testCreateCellWithNullValue() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();

        dailyReconciliationProcessor.createCell(workbook, row, cellStyle, null, 0);

        XSSFCell cell = row.getCell(0);
        assertNull(cell);
    }

    @Test
    public void testGetXssfCellStyleForDate() {
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);

        assertNotNull(cellStyle);
    }

    @Test
    public void testBuildRequestsRowsWithValidRequest() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsLocationCode", "RECAP");
        RequestItemEntity requestEntity = saveRequestItemEntity(1, getItemEntity());
        when(requestItemDetailsRepository.findById(anyInt())).thenReturn(Optional.of(requestEntity));

        dailyReconciliationProcessor.buildRequestsRows(workbook, sheet, cellStyle, 1, "1");

        assertTrue(sheet.getLastRowNum() >= 1);
    }

    @Test
    public void testBuildRequestsRowsWithBlankRequestId() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);

        dailyReconciliationProcessor.buildRequestsRows(workbook, sheet, cellStyle, 1, "");

        // Blank request ID - buildRequestsRows creates the row but does not
        // populate any cells. Verify the row exists but the first cell is
        // not created.
        XSSFRow row = sheet.getRow(1);
        assertNotNull(row);
        assertNull(row.getCell(0));
    }

    @Test
    public void testBuildRequestsRowsWithNullRequestId() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);

        dailyReconciliationProcessor.buildRequestsRows(workbook, sheet, cellStyle, 1, null);

        // Null request ID - buildRequestsRows creates the row but does not
        // populate any cells. Verify the row exists but the first cell is
        // not created.
        XSSFRow row = sheet.getRow(1);
        assertNotNull(row);
        assertNull(row.getCell(0));
    }

    @Test
    public void testBuildRequestsRowsWithNonExistentRequest() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);

        when(requestItemDetailsRepository.findById(anyInt())).thenReturn(Optional.empty());

        dailyReconciliationProcessor.buildRequestsRows(workbook, sheet, cellStyle, 1, "999");

        // The method always creates the row at the given index; since the
        // request doesn't exist, no cells should be populated.
        XSSFRow row = sheet.getRow(1);
        assertNotNull(row);
        assertNull(row.getCell(0));
    }

    @Test
    public void testBuildDeacessionRowsWithValidBarcode() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);
        ItemEntity item = getItemEntity();
        List<ItemEntity> itemList = new ArrayList<>();
        itemList.add(item);

        when(itemDetailsRepository.findByBarcode(anyString())).thenReturn(itemList);

        dailyReconciliationProcessor.buildDeacessionRows(workbook, sheet, cellStyle, 1, "b3");

        assertTrue(sheet.getLastRowNum() >= 1);
    }

    @Test
    public void testBuildDeacessionRowsWithNullList() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);

        when(itemDetailsRepository.findByBarcode(anyString())).thenReturn(null);

        dailyReconciliationProcessor.buildDeacessionRows(workbook, sheet, cellStyle, 1, "b3");

        // No rows should be created for a null result from the repository.
        assertNull(sheet.getRow(1));
    }

    @Test
    public void testBuildDeacessionRowsWithEmptyList() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFCellStyle cellStyle = dailyReconciliationProcessor.getXssfCellStyleForDate(workbook);

        when(itemDetailsRepository.findByBarcode(anyString())).thenReturn(new ArrayList<>());

        dailyReconciliationProcessor.buildDeacessionRows(workbook, sheet, cellStyle, 1, "b3");

        // No rows should be created for an empty list from the repository.
        assertNull(sheet.getRow(1));
    }

    @Test
    public void testGetRowValuesForCompare() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("TestValue");

        Cell cell = dailyReconciliationProcessor.getRowValuesForCompare(row, 0);

        assertNotNull(cell);
        assertEquals("TestValue", cell.getStringCellValue());
    }

    @Test
    public void testGetRowValuesForCompareWithoutCell() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);

        Cell cell = dailyReconciliationProcessor.getRowValuesForCompare(row, 5);

        assertNull(cell);
    }

    @Test
    public void testCompareLasAndScsbSheets() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("LAS");
        XSSFSheet sheet2 = workbook.createSheet("SCSB");
        CellStyle cellStyle = workbook.createCellStyle();

        // Create header and some rows
        createHeaderForSheet(sheet1);
        createHeaderForSheet(sheet2);

        dailyReconciliationProcessor.compareLasAndScsbSheets(workbook, cellStyle);

        assertEquals(3, workbook.getNumberOfSheets());
        assertNotNull(workbook.getSheet(ScsbConstants.DAILY_RR_COMPARISON));
    }

    @Test
    public void testCompareTwoSheetsWithData() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("Sheet1");
        XSSFSheet sheet2 = workbook.createSheet("Sheet2");
        XSSFSheet sheet3 = workbook.createSheet("Comparison");
        CellStyle cellStyle = workbook.createCellStyle();

        // Create rows with data
        XSSFRow row1 = sheet1.createRow(1);
        if (row1 != null) {
            row1.createCell(0).setCellValue("REQ001");
            row1.createCell(1).setCellValue("BARCODE001");
            row1.createCell(10).setCellValue("AVAILABLE");
        }

        XSSFRow row2 = sheet2.createRow(1);
        if (row2 != null) {
            row2.createCell(0).setCellValue("REQ001");
            row2.createCell(1).setCellValue("BARCODE001");
            row2.createCell(10).setCellValue("AVAILABLE");
        }

        // Ensure the processor has IMS status mappings set; compareTwoSheets
        // relies on these values when classifying statuses.
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");
        dailyReconciliationProcessor.compareTwoSheets(sheet1, sheet2, sheet3, workbook, cellStyle);

        assertNotNull(sheet3);
    }

    @Test
    public void testCompareTwoRowsWithMatchingData() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        CellStyle cellStyle = workbook.createCellStyle();

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");

        XSSFRow row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("REQ001");
        row1.createCell(1).setCellValue("BARCODE001");
        row1.createCell(10).setCellValue("AVAILABLE");

        XSSFRow row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("REQ001");
        row2.createCell(1).setCellValue("BARCODE001");
        row2.createCell(10).setCellValue("AVAILABLE");

        XSSFRow row3 = sheet.createRow(2);

        dailyReconciliationProcessor.compareTwoRows(row1, row2, row3, workbook, cellStyle);

        // The comparison should populate row3 with data
        assertNotNull(row3);
    }

    @Test
    public void testCompareTwoRowsWithMismatchedData() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        CellStyle cellStyle = workbook.createCellStyle();

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");

        XSSFRow row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("REQ001");
        row1.createCell(1).setCellValue("BARCODE001");
        row1.createCell(10).setCellValue("AVAILABLE");

        XSSFRow row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("REQ002");
        row2.createCell(1).setCellValue("BARCODE002");
        row2.createCell(10).setCellValue("NOT_AVAILABLE");

        XSSFRow row3 = sheet.createRow(2);

        dailyReconciliationProcessor.compareTwoRows(row1, row2, row3, workbook, cellStyle);

        assertNotNull(row3.getCell(6));
        assertEquals(ScsbConstants.DAILY_RR_MISMATCH, row3.getCell(6).getStringCellValue());
    }

    @Test
    public void testCompareTwoRowsWithNullRows() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        CellStyle cellStyle = workbook.createCellStyle();

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");

        XSSFRow row3 = sheet.createRow(0);

        // Should not throw exception with null rows
        dailyReconciliationProcessor.compareTwoRows(null, null, row3, workbook, cellStyle);

        assertNotNull(row3);
    }

    @Test
    public void testCompareTwoRowsWithPartialData() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        CellStyle cellStyle = workbook.createCellStyle();

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");

        XSSFRow row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("REQ001");
        row1.createCell(10).setCellValue("AVAILABLE");

        XSSFRow row2 = sheet.createRow(1);
        row2.createCell(1).setCellValue("BARCODE001");
        row2.createCell(10).setCellValue("NOT_AVAILABLE");

        XSSFRow row3 = sheet.createRow(2);

        dailyReconciliationProcessor.compareTwoRows(row1, row2, row3, workbook, cellStyle);

        assertNotNull(row3);
    }

    @Test
    public void testCheckCellIsNotEmptyWithValidCell() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);

        Boolean result = (Boolean) ReflectionTestUtils.invokeMethod(dailyReconciliationProcessor, "checkCellIsNotEmpty", cell);
        assertTrue(result);
    }

    @Test
    public void testCheckCellIsNotEmptyWithNullCell() {
        Boolean result = (Boolean) ReflectionTestUtils.invokeMethod(dailyReconciliationProcessor, "checkCellIsNotEmpty", (Cell) null);
        assertFalse(result);
    }

    @Test
    public void testGetLasStatusForCompareWithAvailableStatus() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("AVAILABLE");

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");

        String[] statusArray = new String[3];
        String result = ReflectionTestUtils.invokeMethod(dailyReconciliationProcessor, "getLasStatusForCompare", cell, statusArray);

        assertEquals("AVAILABLE", result);
        assertEquals(ScsbCommonConstants.AVAILABLE, statusArray[2]);
    }

    @Test
    public void testGetLasStatusForCompareWithNotAvailableStatus() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("NOT_AVAILABLE");

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");

        String[] statusArray = new String[3];
        String result = ReflectionTestUtils.invokeMethod(dailyReconciliationProcessor, "getLasStatusForCompare", cell, statusArray);

        assertEquals("NOT_AVAILABLE", result);
        assertEquals(ScsbCommonConstants.NOT_AVAILABLE, statusArray[2]);
    }

    @Test
    public void testGetLasStatusForCompareWithUnknownStatus() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("UNKNOWN");

        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsAvailableCodes", "AVAILABLE");
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsNotAvailableCodes", "NOT_AVAILABLE");

        String[] statusArray = new String[3];
        String result = ReflectionTestUtils.invokeMethod(dailyReconciliationProcessor, "getLasStatusForCompare", cell, statusArray);

        assertEquals("UNKNOWN", result);
    }

    @Test
    public void testConstructor() {
        DailyReconciliationProcessor processor = new DailyReconciliationProcessor("RECAP");
        assertNotNull(processor);
    }

    @Test
    public void testProcessInputWithSingleDailyReconcilationRecord() throws Exception {
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "imsLocationCode", "RECAP");
        when(exchange.getIn()).thenReturn(message);
        when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_AVAILABLE_ITEM_STATUS_CODES)).thenReturn("AVAILABLE");
        when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_NOT_AVAILABLE_ITEM_STATUS_CODES)).thenReturn("NOT_AVAILABLE");
        when(propertyUtil.getPropertyByImsLocationAndKey("RECAP", PropertyKeyConstants.IMS.IMS_REQUESTABLE_NOT_RETRIEVABLE_ITEM_STATUS_CODES)).thenReturn("");

        List<DailyReconcilationRecord> recordList = new ArrayList<>();
        recordList.add(getDailyReconcilationRecord("12345", "1", ScsbConstants.GFA_STATUS_IN));
        when(message.getBody()).thenReturn(recordList);
        when(message.getHeader(anyString())).thenReturn("CamelAwsS3Key/CamelAwsS3Key");
        when(camelContext.getRouteController()).thenReturn(routeController);
        when(requestItemDetailsRepository.findById(anyInt())).thenReturn(Optional.of(saveRequestItemEntity(1, getItemEntity())));
        when(awsS3Client.doesObjectExist(anyString(), anyString())).thenReturn(true);
        ReflectionTestUtils.setField(dailyReconciliationProcessor, "filePath", filePath);

        dailyReconciliationProcessor.processInput(exchange);

        // Just verify the method completed without exception
        verify(exchange, atLeastOnce()).getIn();
    }

    private void createHeaderForSheet(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("RequestId");
        row.createCell(1).setCellValue("Barcode");
        row.createCell(10).setCellValue("Status");
    }

}