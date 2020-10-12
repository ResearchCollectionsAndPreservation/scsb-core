package org.recap.service.accession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccessionResolverFactory {

    private final List<AccessionResolverAbstract> accessionResolverAbstractList;

    @Autowired
    public AccessionResolverFactory(List<AccessionResolverAbstract> accessionResolverAbstractList) {

        this.accessionResolverAbstractList = accessionResolverAbstractList;
    }

    public AccessionInterface getFormatResolver(String format){
        return accessionResolverAbstractList
                .stream()
                .filter(formatResolver -> formatResolver.isFormat(format))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
