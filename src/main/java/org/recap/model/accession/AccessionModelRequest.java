package org.recap.model.accession;

import lombok.Data;
import java.util.List;

@Data
public class AccessionModelRequest {
    private String imsLocationCode;
    private List<AccessionRequest> accessionRequests;
}
