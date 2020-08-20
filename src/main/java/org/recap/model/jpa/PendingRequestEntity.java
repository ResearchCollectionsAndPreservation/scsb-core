package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "PENDING_REQUEST_T", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "PENDING_ID"))
public class PendingRequestEntity extends AbstractEntity<Integer>  {
    @Column(name = "REQUEST_ID")
    private Integer requestId;

    @Column(name = "ITEM_ID")
    private Integer itemId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUEST_CREATED_DATE")
    private Date requestCreatedDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUEST_ID", referencedColumnName = "REQUEST_ID", insertable = false, updatable = false)
    private RequestItemEntity requestItemEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ITEM_ID", insertable = false, updatable = false)
    private ItemEntity itemEntity;
}
