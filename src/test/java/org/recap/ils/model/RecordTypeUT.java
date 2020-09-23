package org.recap.ils.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class RecordTypeUT {

    @Test
    public void getRecordType(){
        RecordType recordType = new RecordType();
        DataFieldType dataFieldType = new DataFieldType();
        SubfieldatafieldType subfieldatafieldType = new SubfieldatafieldType();
        subfieldatafieldType.setId("1");
        subfieldatafieldType.setCode("PA");
        subfieldatafieldType.setValue("2366");
        assertNotNull(subfieldatafieldType.getId());
        assertNotNull(subfieldatafieldType.getCode());
        assertNotNull(subfieldatafieldType.getValue());
        dataFieldType.setId("1");
        dataFieldType.setInd1("F100R8");
        dataFieldType.setInd2("F100GR46");
        dataFieldType.setSubfield(Arrays.asList(subfieldatafieldType));
        dataFieldType.setTag("TG001");
        assertNotNull(dataFieldType.getId());
        assertNotNull(dataFieldType.getInd1());
        assertNotNull(dataFieldType.getInd2());
        assertNotNull(dataFieldType.getSubfield());
        assertNotNull(dataFieldType.getTag());
        LeaderFieldType leaderFieldType = new LeaderFieldType();
        leaderFieldType.setId("1");
        leaderFieldType.setValue("24556");
        assertNotNull(leaderFieldType.getId());
        assertNotNull(leaderFieldType.getValue());
        RecordTypeType.fromValue("Bibliographic");
        RecordTypeType.values();
        recordType.setId("1");
        recordType.setDatafield(Arrays.asList(dataFieldType));
        recordType.setLeader(leaderFieldType);
        recordType.setType(RecordTypeType.AUTHORITY);
        assertNotNull(recordType.getControlfield());
        assertNotNull(recordType.getDatafield());
        assertNotNull(recordType.getId());
        assertNotNull(recordType.getLeader());
        assertNotNull(recordType.getType());
    }
}
