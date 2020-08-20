package org.recap.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by angelind on 29/7/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestInstitutionBibPK implements Serializable {
    private Integer itemId;
    private Integer owningInstitutionId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RequestInstitutionBibPK requestInstitutionBibPK = (RequestInstitutionBibPK) o;

        if (!Objects.equals(itemId, requestInstitutionBibPK.itemId))
            return false;
        return owningInstitutionId != null ? owningInstitutionId.equals(requestInstitutionBibPK.owningInstitutionId) : requestInstitutionBibPK.owningInstitutionId == null;

    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + (owningInstitutionId != null ? owningInstitutionId.hashCode() : 0);
        return result;
    }

}
