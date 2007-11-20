package org.openelis.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="lock")
public class Lock {
	    
    @Id 
    @GeneratedValue
    @Column(name="id")
    private Integer id;
    @Column(name="reference_table")
    private Integer referenceTable;
    @Column(name="reference_id")
    private Integer referenceId;
    @Column(name="expires")
    private Date expires;
    @Column(name="system_user")
    private Integer systemUser;
    
    public Date getExpires() {
        return expires;
    }
    public void setExpires(Date expires) {
        this.expires = expires;
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
}
