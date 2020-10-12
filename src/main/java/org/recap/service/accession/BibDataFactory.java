package org.recap.service.accession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BibDataFactory {

    private final List<BibDataAbstract> bibDataAbstractList;

    @Autowired
    public BibDataFactory(List<BibDataAbstract> bibDataAbstractList) {

        this.bibDataAbstractList = bibDataAbstractList;
    }

    public BibDataForAccessionInterface getAuth(String auth){
        return bibDataAbstractList
                .stream()
                .filter(bibData -> bibData.isAuth(auth))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
