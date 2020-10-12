package org.recap.service.accession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public abstract class BibDataAbstract implements BibDataForAccessionInterface {

    @Value("${bibdata.api.connection.timeout}")
    Integer connectionTimeout;

    @Value("${bibdata.api.read.timeout}")
    Integer readTimeout;

    public abstract boolean isAuth(String auth);

}
