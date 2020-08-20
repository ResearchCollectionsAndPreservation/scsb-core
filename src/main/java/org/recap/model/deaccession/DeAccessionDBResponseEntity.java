package org.recap.model.deaccession;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by chenchulakshmig on 3/10/16.
 */
@Setter
@Getter
public class DeAccessionDBResponseEntity {

    private String barcode;

    private String deliveryLocation;

    private String itemStatus;

    private String customerCode;

    private String status;

    private String reasonForFailure;

    private String institutionCode;

    private String collectionGroupCode;

    private List<String> owningInstitutionBibIds;

    private Integer itemId;

    private List<Integer> bibliographicIds;

    private List<Integer> holdingIds;

}
