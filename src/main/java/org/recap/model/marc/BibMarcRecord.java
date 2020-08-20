package org.recap.model.marc;

import lombok.Getter;
import lombok.Setter;
import org.marc4j.marc.Record;

import java.util.List;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
@Setter
@Getter
public class BibMarcRecord {
    /**
     * The Bib record.
     */
    Record bibRecord;
    /**
     * The Holdings marc records.
     */
    List<HoldingsMarcRecord> holdingsMarcRecords;

}
