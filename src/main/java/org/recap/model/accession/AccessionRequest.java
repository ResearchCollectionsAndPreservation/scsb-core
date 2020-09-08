package org.recap.model.accession;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by chenchulakshmig on 19/10/16.
 */
@Data
@EqualsAndHashCode(of = {"itemBarcode"})
public class AccessionRequest {
    private String itemBarcode;
    private String customerCode;
}
