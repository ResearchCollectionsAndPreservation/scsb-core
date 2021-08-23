package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by hemalathas on 19/5/17.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class StatusReconciliationCSVRecord implements Serializable{

    @DataField(pos = 1, columnName = "Barcode")
    private String barcode;

    @DataField(pos = 2, columnName = "RequestAvailability")
    private String requestAvailability;

    @DataField(pos = 3, columnName = "OwningInstitution")
    private String owningInstitution;

    @DataField(pos = 4, columnName = "RequestingInstitution")
    private String requestingInstitution;

    @DataField(pos = 5, columnName = "RequestId")
    private String requestId;

    @DataField(pos = 6, columnName = "StatusInScsb")
    private String statusInScsb;

    @DataField(pos = 7, columnName = "StatusInLas")
    private String statusInLas;

    @DataField(pos = 8, columnName = "ImsLocation")
    private String imsLocation;

    @DataField(pos = 9, columnName = "RequestedDateTime")
    private String requestedDateTime;

    @DataField(pos = 10, columnName = "UpdatedDateTime")
    private String updatedDateTime;

    @DataField(pos = 11, columnName = "ReconciliationStatus")
    private String reconciliationStatus;
}
