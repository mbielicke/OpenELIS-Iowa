package org.openelis.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({@NamedQuery(name="getEntries", query="from History where referenceId = :referenceId and referenceTable = :referenceTable")})

@Entity
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "reference_id")
    private Integer referenceId;
    @Column(name = "reference_table")
    private Integer referenceTable;
    @Column(name = "timestamp")
    private Date timestamp;
    @Column(name = "activity")
    private Integer activity;
    @Column(name = "system_user")
    private Integer systemUser;
    @Lob
    @Column(name = "changes")
    private String changes;

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getReferenceTable() {
        return referenceTable;
    }

    public void setReferenceTable(Integer referenceTable) {
        this.referenceTable = referenceTable;
    }

    public Integer getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(Integer systemUser) {
        this.systemUser = systemUser;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
