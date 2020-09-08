package org.recap.model.accession;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by premkb on 8/3/17.
 */
@Data
@EqualsAndHashCode(of = {"itemBarcode"})
public class AccessionResponse {
    private String itemBarcode;
    private String message;
}
