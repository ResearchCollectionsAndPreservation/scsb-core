package org.recap.converter;

import org.recap.model.accession.AccessionRequest;

import java.util.Map;

@FunctionalInterface
public interface AccessionXmlToBibEntityConverterInterface {

    /**
     * This method is used to convert the given record and put them in a map.
     *
     * @param record          the record
     * @param institutionName the institution name
     * @param accessionRequest    the accessionRequest
     * @return the map
     */
    public Map convert(Object record, String institutionName, AccessionRequest accessionRequest);
}
