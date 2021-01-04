package org.recap.camel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by chenchulakshmig on 15/9/16.
 */
@Getter
@Setter
public class EmailPayLoad implements Serializable{

    private String to;
    private String cc;
    private String subject;
    private String itemBarcode;
    private String patronBarcode;
    private String customerCode;
    private String messageDisplay;
    private String location;
    private String institution;
    private String reportFileName;
    private String xmlFileName;
    private Exception exception;
    private String exceptionMessage;
    private String pendingRequestLimit;

    private String bulkRequestId;
    private String bulkRequestName;
    private String bulkRequestFileName;
    private String bulkRequestStatus;
    private String bulkRequestCsvFileData;

}
