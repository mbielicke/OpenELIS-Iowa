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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table cron.
 */

public class CronDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected String          cronTab, bean, method, parameters, name, isActive;
    protected Datetime        lastRunDate;

    public CronDO() {

    }

    public CronDO(Integer id, String cronTab, String name, String isActive, String bean,
                  String method, String parameters, Date lastRunDate) {
        setId(id);
        setCronTab(cronTab);
        setName(name);
        setIsActive(isActive);
        setBean(bean);
        setMethod(method);
        setParameters(parameters);
        setLastRunDate(DataBaseUtil.toYM(lastRunDate));
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public String getCronTab() {
        return cronTab;
    }

    public void setCronTab(String cronTab) {
        this.cronTab = DataBaseUtil.trim(cronTab);
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = DataBaseUtil.trim(bean);
        _changed = true;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = DataBaseUtil.trim(method);
        _changed = true;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = DataBaseUtil.trim(parameters);
        _changed = true;
    }

    public Datetime getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(Datetime lastRun) {
        this.lastRunDate = DataBaseUtil.toYM(lastRun);
        _changed = true;
    }
}