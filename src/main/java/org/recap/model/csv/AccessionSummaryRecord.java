package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by hemalathas on 22/11/16.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class AccessionSummaryRecord implements Serializable {

    @DataField(pos = 1, columnName = "Count of new bibs loaded")
    private String successBibCount;

    @DataField(pos = 2, columnName = "Failed new additions - Bibs")
    private String failedBibCount;

    @DataField(pos = 3, columnName = "Bib-Reason for Failure")
    private String reasonForFailureBib;

    @DataField(pos = 4, columnName = "Number of bibs that match existing from same inst")
    private String noOfBibMatches;

    @DataField(pos = 5, columnName = "Count of New Items Loaded")
    private String successItemCount;

    @DataField(pos = 6, columnName = "Failed new additions - Items")
    private String failedItemCount;

    @DataField(pos = 7, columnName = "Item-Reason for Failure")
    private String reasonForFailureItem;
}
