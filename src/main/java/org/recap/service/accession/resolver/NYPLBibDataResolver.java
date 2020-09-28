package org.recap.service.accession.resolver;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jaxb.*;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.service.accession.BulkAccessionService;
import org.recap.service.partnerservice.NYPLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sheiks on 26/05/17.
 */
@Service
public class NYPLBibDataResolver extends BibDataResolver {

    private static final Logger logger = LoggerFactory.getLogger(NYPLBibDataResolver.class);

    @Autowired
    private NYPLService nyplService;

    public NYPLBibDataResolver(BulkAccessionService bulkAccessionService) {
        super(bulkAccessionService);
    }

    @Override
    public boolean isInterested(String institution) {
        return RecapCommonConstants.NYPL.equals(institution);
    }

    @Override
    public String getBibData(String itemBarcode, String customerCode) {
        return nyplService.getBibData(itemBarcode, customerCode);
    }

    @Override
    public Object unmarshal(String bibDataResponse) {
        BibRecords bibRecords = null;
        try {
            JAXBContext context = JAXBContext.newInstance(BibRecords.class);
            XMLInputFactory xif = XMLInputFactory.newFactory();
            xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            InputStream stream = new ByteArrayInputStream(bibDataResponse.getBytes(StandardCharsets.UTF_8));
            XMLStreamReader xsr = null;
            try {
                xsr = xif.createXMLStreamReader(stream);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            Unmarshaller um = context.createUnmarshaller();
            bibRecords = (BibRecords) um.unmarshal(xsr);
        } catch (JAXBException e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
        return bibRecords;
    }

    @Override
    public ItemEntity getItemEntityFromRecord(Object object, Integer owningInstitutionId) {
        BibRecords bibRecords = (BibRecords) object;
        List<BibRecord> bibRecordList = bibRecords.getBibRecordList();
        if(CollectionUtils.isNotEmpty(bibRecordList)) {
            String owningInstitutionItemId = getOwningInstitutionItemIdFromBibRecord(bibRecordList.get(0));
            if(StringUtils.isNotBlank(owningInstitutionItemId)) {
                return itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(owningInstitutionItemId,owningInstitutionId);
            }
        }
        return null;
    }

    private String getOwningInstitutionItemIdFromBibRecord(BibRecord bibRecord) {
        List<Holdings> holdings = bibRecord.getHoldings();
        if(CollectionUtils.isNotEmpty(holdings)) {
            for (Iterator<Holdings> iterator = holdings.iterator(); iterator.hasNext(); ) {
                Holdings holdingsRecord =  iterator.next();
                List<Holding> holdingList = holdingsRecord.getHolding();
                if(CollectionUtils.isNotEmpty(holdingList)) {
                    for (Iterator<Holding> holdingIterator = holdingList.iterator(); holdingIterator.hasNext(); ) {
                        Holding holding = holdingIterator.next();
                        List<Items> items = holding.getItems();
                        if(CollectionUtils.isNotEmpty(items)) {
                            for (Iterator<Items> itemsIterator = items.iterator(); itemsIterator.hasNext(); ) {
                                Items item = itemsIterator.next();

                                ContentType itemContent = item.getContent();
                                CollectionType itemContentCollection = itemContent.getCollection();

                                List<RecordType> itemRecordTypes = itemContentCollection.getRecord();

                                if(CollectionUtils.isNotEmpty(itemRecordTypes)) {
                                    for (Iterator<RecordType> recordTypeIterator = itemRecordTypes.iterator(); recordTypeIterator.hasNext(); ) {
                                        RecordType recordType = recordTypeIterator.next();
                                        return marcUtil.getDataFieldValueForRecordType(recordType,
                                                "876", null, null, "a");

                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String processXml(Set<AccessionResponse> accessionResponses, Object object, List<Map<String, String>> responseMapList, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) throws Exception {
        return bulkAccessionService.processAccessionForSCSBXml(accessionResponses, object,
                responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
    }
}
