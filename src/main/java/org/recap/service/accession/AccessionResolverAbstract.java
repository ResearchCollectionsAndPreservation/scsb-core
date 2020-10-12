package org.recap.service.accession;

import org.apache.commons.lang3.StringUtils;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AccessionResolverAbstract implements AccessionInterface {

    @Autowired
    MarcUtil marcUtil;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    BibDataFactory bibDataFactory;

    @Autowired
    PropertyUtil propertyUtil;

    @Autowired
    AccessionUtil accessionUtil;

    @Autowired
    AccessionValidationService accessionValidationService;

    public boolean isAccessionProcess(ItemEntity itemEntity, String owningInstitution) {
        if(null != itemEntity) {
            InstitutionEntity institutionEntity = itemEntity.getInstitutionEntity();
            return !StringUtils.equals(owningInstitution, institutionEntity.getInstitutionCode());
        }
        return true;
    }

    public abstract boolean isFormat(String format);
}
