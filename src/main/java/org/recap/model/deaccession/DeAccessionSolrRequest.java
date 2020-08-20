package org.recap.model.deaccession;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 15/2/17.
 */
@Setter
@Getter
public class DeAccessionSolrRequest {

    private List<Integer> bibIds = new ArrayList<>();
    private List<Integer> holdingsIds = new ArrayList<>();
    private List<Integer> itemIds = new ArrayList<>();
    private String status;
}
