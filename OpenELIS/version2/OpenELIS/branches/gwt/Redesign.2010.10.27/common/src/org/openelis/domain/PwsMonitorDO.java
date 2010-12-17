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
 * Class represents the fields in database table pws_monitor.
 */

public class PwsMonitorDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, tinwsysIsNumber, numberSamples;

    protected String          stAsgnIdentCd, name, tiaanlgpTiaanlytName, frequencyName, periodName;

    protected Datetime        compBeginDate, compEndDate;
    
    public PwsMonitorDO() {     
    }

    public PwsMonitorDO(Integer id, Integer tinwsysIsNumber, String stAsgnIdentCd,
                        String name, String tiaanlgpTiaanlytName, Integer numberSamples,
                        Date compBeginDate, Date compEndDate,
                        String frequencyName, String periodName) {   
        setId(id);
        setTinwsysIsNumber(tinwsysIsNumber);
        setStAsgnIdentCd(stAsgnIdentCd);
        setName(name);
        setTiaanlgpTiaanlytName(tiaanlgpTiaanlytName);
        setNumberSamples(numberSamples);
        setCompBeginDate(DataBaseUtil.toYD(compBeginDate));
        setCompEndDate(DataBaseUtil.toYD(compEndDate));
        setFrequencyName(frequencyName); 
        setPeriodName(periodName);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        this.tinwsysIsNumber = tinwsysIsNumber;
        _changed = true;
    }
    
    public String getStAsgnIdentCd() {
        return stAsgnIdentCd;
    }

    public void setStAsgnIdentCd(String stAsgnIdentCd) {
        this.stAsgnIdentCd = DataBaseUtil.trim(stAsgnIdentCd);
        _changed = true;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }
    
    public String getTiaanlgpTiaanlytName() {
        return tiaanlgpTiaanlytName;
    }

    public void setTiaanlgpTiaanlytName(String tiaanlgpTiaanlytName) {
        this.tiaanlgpTiaanlytName = DataBaseUtil.trim(tiaanlgpTiaanlytName);
        _changed = true;
    }

    public Integer getNumberSamples() {
        return numberSamples;
    }

    public void setNumberSamples(Integer numberSamples) {
        this.numberSamples = numberSamples;
        _changed = true;
    }
    
    public Datetime getCompBeginDate() {
        return compBeginDate;
    }

    public void setCompBeginDate(Datetime compBeginDate) {
        this.compBeginDate = DataBaseUtil.toYD(compBeginDate);
        _changed = true;
    }

    public Datetime getCompEndDate() {
        return compEndDate;
    }

    public void setCompEndDate(Datetime compEndDate) {
        this.compEndDate = DataBaseUtil.toYD(compEndDate);
        _changed = true;
    }

    public String getFrequencyName() {
        return frequencyName;
    }

    public void setFrequencyName(String frequencyName) {
        this.frequencyName = DataBaseUtil.trim(frequencyName);
        _changed = true;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = DataBaseUtil.trim(periodName);
        _changed = true;
    }

}
