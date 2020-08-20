package org.recap.model.report;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by premkb on 25/12/16.
 */
@Setter
@Getter
public class SubmitCollectionReportInfo {

    private String itemBarcode;

    private String customerCode;

    private String owningInstitution;

    private String message;

}
