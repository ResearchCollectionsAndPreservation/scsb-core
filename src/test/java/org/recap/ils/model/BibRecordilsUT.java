package org.recap.ils.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BibRecordilsUT extends BaseTestCaseUT {

    @Test
    public void recordType() throws Exception {
        BibRecord bibRecord=new BibRecord();
        bibRecord.setHoldings(Arrays.asList(getHoldings()));
        bibRecord.setBib(getBib());
        assertNotNull(bibRecord.getBib());
        assertNotNull(bibRecord.getHoldings());
    }

    private Holdings getHoldings() {
        Holdings holdings=new Holdings();
        holdings.setHolding(Arrays.asList(getHolding()));
        assertNotNull(holdings.getHolding());
        return holdings;
    }

    private Holding getHolding() {
        Holding holding=new Holding();
        holding.setContent(getContentType());
        holding.setOwningInstitutionHoldingsId("1");
        holding.setItems(Arrays.asList(getItems()));
        assertNotNull(holding.getContent());
        assertNotNull(holding.getItems());
        assertNotNull(holding.getOwningInstitutionHoldingsId());
        return holding;
    }

    private Items getItems() {
        Items items=new Items();
        items.setContent(getContentType());
        assertNotNull(items.getContent());
        return items;
    }

    private Bib getBib() {
        Bib bib=new Bib();
        bib.setContent(getContentType());
        bib.setOwningInstitutionBibId("1");
        bib.setOwningInstitutionId("2");
        assertNotNull(bib.getContent());
        assertNotNull(bib.getOwningInstitutionBibId());
        assertNotNull(bib.getOwningInstitutionId());
        return bib;
    }

    private ContentType getContentType() {
        ContentType contentType=new ContentType();
        contentType.setCollection(getCollectionType());
        assertNotNull(contentType.getCollection());
        return contentType;
    }

    private CollectionType getCollectionType() {
        CollectionType collectionType=new CollectionType();
        collectionType.setId("1");
        collectionType.setRecord(Arrays.asList(getRecordType()));
        assertNotNull(collectionType.getId());
        assertNotNull(collectionType.getRecord());
        return collectionType;
    }

    private RecordType getRecordType() {
        RecordType recordType=new RecordType();
        recordType.setId("1");
        recordType.setDatafield(Arrays.asList(getDataFieldType()));
        recordType.setLeader(getLeaderFieldType());
        assertNotNull(recordType.getId());
        assertNull(recordType.getType());
        assertNotNull(recordType.getControlfield());
        assertNotNull(recordType.getDatafield());
        assertNotNull(recordType.getLeader());

        ControlFieldType controlFieldType=new ControlFieldType();
        controlFieldType.setId("1");
        controlFieldType.setTag("1");
        controlFieldType.setValue("1");
        assertNotNull(controlFieldType.getId());
        assertNotNull(controlFieldType.getTag());
        assertNotNull(controlFieldType.getValue());

        return recordType;
    }

    private LeaderFieldType getLeaderFieldType() {
        LeaderFieldType leaderFieldType=new LeaderFieldType();
        leaderFieldType.setId("1");
        leaderFieldType.setValue("4");
        assertNotNull(leaderFieldType.getId());
        assertNotNull(leaderFieldType.getValue());
        return leaderFieldType;
    }

    private DataFieldType getDataFieldType() {
        DataFieldType dataFieldType=new DataFieldType();
        dataFieldType.setId("1");
        dataFieldType.setInd1("1");
        dataFieldType.setInd2("2");
        dataFieldType.setTag("d");
        dataFieldType.setSubfield(Arrays.asList(getSubfieldatafieldType()));
        assertNotNull(dataFieldType.getId());
        assertNotNull(dataFieldType.getInd1());
        assertNotNull(dataFieldType.getInd2());
        assertNotNull(dataFieldType.getSubfield());
        assertNotNull(dataFieldType.getTag());
        return dataFieldType;
    }

    private SubfieldatafieldType getSubfieldatafieldType() {
        SubfieldatafieldType subfieldatafieldType=new SubfieldatafieldType();
        subfieldatafieldType.setCode("1");
        subfieldatafieldType.setId("3");
        subfieldatafieldType.setValue("1");
        assertNotNull(subfieldatafieldType.getCode());
        assertNotNull(subfieldatafieldType.getId());
        assertNotNull(subfieldatafieldType.getValue());
        return subfieldatafieldType;
    }
}
