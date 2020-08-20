package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 10/10/17.
 */
@Setter
@Getter
public class BulkRequestItem {

    private String itemBarcode;
    private String customerCode;
    private String requestId;
    private String requestStatus;
    private String status;

}
