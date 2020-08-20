package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;
import org.recap.model.AbstractResponseItem;

/**
 * Created by sudhishk on 15/12/16.
 */
@Setter
@Getter
public class ItemRefileResponse extends AbstractResponseItem {

    private Integer requestId;
    private String jobId;

}
