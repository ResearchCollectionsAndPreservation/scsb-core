package org.recap.model.request;

import lombok.Data;

import java.util.List;

/**
 * Created by sudhishk on 15/12/16.
 */
@Data
public class ItemCheckInRequest {
    private List<String> itemBarcodes;
    private String itemOwningInstitution=""; // PUL, CUL, NYPL
    private String patronIdentifier;
}
