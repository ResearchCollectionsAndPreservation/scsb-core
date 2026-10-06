package org.recap.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.RequestInstitutionBibPK;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 23/3/17.
 */
public class RequestInstitutionBibPKUT extends BaseTestCaseUT {

    @Test
    public void testRequestInstitutionBibPK() {
        RequestInstitutionBibPK requestInstitutionBibPK = new RequestInstitutionBibPK();
        requestInstitutionBibPK.setItemId(1);
        requestInstitutionBibPK.setOwningInstitutionId(1);
        requestInstitutionBibPK.equals(new RequestInstitutionBibPK());
        requestInstitutionBibPK.equals(requestInstitutionBibPK);
        requestInstitutionBibPK.equals(null);
        requestInstitutionBibPK.equals(RequestInstitutionBibPK.class);
        requestInstitutionBibPK.hashCode();
        new RequestInstitutionBibPK().hashCode();
        RequestInstitutionBibPK requestInstitutionBibPK1 = new RequestInstitutionBibPK(1, 1);
        assertNotNull(requestInstitutionBibPK1.getOwningInstitutionId());
        assertNotNull(requestInstitutionBibPK1.getItemId());
    }

}