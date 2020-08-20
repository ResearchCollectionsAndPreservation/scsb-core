package org.recap.model.submitcollection;

import lombok.Getter;
import lombok.Setter;
import org.recap.model.jpa.BibliographicEntity;

import java.util.List;

/**
 * Created by premkb on 22/10/17.
 */
@Setter
@Getter
public class NonBoundWithBibliographicEntityObject {
    private String owningInstitutionBibId;
    private List<BibliographicEntity> bibliographicEntityList;
}
