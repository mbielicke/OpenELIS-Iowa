/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.entity;

/**
 * Cron Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;

@NamedQueries( {
    @NamedQuery( name="Cron.FetchById",
    		     query="select distinct new org.openelis.domain.CronDO(c.id,c.cronTab,c.name,c.isActive,c.bean,c.method,c.parameters,c.lastRun) from "
    		    	 +" Cron c where c.id = :id"),
    @NamedQuery( name="Cron.FetchActive",
    		     query="from Cron where isActive = 'Y'")
})
@Entity
@Table(name = "cron")
@EntityListeners( {AuditUtil.class})
public class Cron {
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                id;
    
    @Column(name="cron_tab")
    private String                 cronTab;
    
    @Column(name="name")
    private String                 name;
    
    @Column(name="is_active")
    private String                 isActive;
    
    @Column(name="bean")
    private String 				   bean;
    
    @Column(name="method")
    private String 				   method;
    
    @Column(name="parameters")
    private String 	               parameters;
    
    @Column(name="last_run")       
    private Date                   lastRun;

    @Transient
    private Cron original;
    
	public Integer getId() {
		return id;
	}

	public String getCronTab() {
		return cronTab;
	}
	

	public void setCronTab(String cronTab) {
		if(DataBaseUtil.isDifferent(cronTab, this.cronTab))
			this.cronTab = cronTab;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if(DataBaseUtil.isDifferent(name,this.name))
			this.name = name;
	}
	
	public String getIsActive() {
		return isActive;
	}
	
	public void setIsActive(String isActive) {
		if(DataBaseUtil.isDifferent(isActive, this.isActive))
			this.isActive = isActive;
	}

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		if(DataBaseUtil.isDifferent(bean, this.bean))
			this.bean = bean;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		if(DataBaseUtil.isDifferent(method, this.method))
			this.method = method;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		if(DataBaseUtil.isDifferent(parameters, this.parameters))
			this.parameters = parameters;
	}
	
	public Datetime getLastRun() {
		return DataBaseUtil.toYM(lastRun);
	}
	
	public void setLastRun(Datetime lastRun) {
		if(DataBaseUtil.isDifferentYM(lastRun, this.lastRun))
			this.lastRun = DataBaseUtil.toDate(lastRun);
	}
    
    public Cron getOriginal() {
        return original;
    }

    public void setOriginal(Cron original) {
    	if(DataBaseUtil.isDifferent(original, this.original))
    		this.original = original;
    }

    public void setClone() {
        try {
            original = (Cron)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.CRON);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
            	 .setField("cron", cronTab, original.cronTab)
            	 .setField("bean", bean, original.bean)
        		 .setField("method", method, original.method)
        		 .setField("parameters", parameters, original.parameters);
        
        return audit;
    }	
}