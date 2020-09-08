package org.recap.model.jpa;

import javax.persistence.*;

/**
 * Created by premkb on 16/8/17.
 *
 * This class is to generate sequence id for owning inst bib id, owning inst holding id, owning inst item id which is used for generate dummy record
 */

@Entity
@Table(name = "OWNING_INST_ID_SEQ", schema = "recap", catalog = "")
public class OwningInstitutionIDSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer ID;

    public Integer getID() {
        return ID;
    }

}
