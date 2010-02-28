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

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table pws.
 */

public class PwsDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, tinwsysIsNumber, dPopulationCount, startDay,
                              startMonth, endDay, endMonth;

    protected String          number0, alternateStNum, name, activityStatusCd, dPrinCitySvdNm,
                              dPrinCntySvdNm, dPwsStTypeCd, activityRsnTxt;

    protected Datetime        effBeginDt, effEndDt;

    public PwsDO() {
    }

    public PwsDO(Integer id, Integer tinwsysIsNumber, String number0, String alternateStNum, String name,
                 String activityStatusCd, String dPrinCitySvdNm, String dPrinCntySvdNm,
                 Integer dPopulationCount, String dPwsStTypeCd, String activityRsnTxt,
                 Integer startDay, Integer startMonth, Integer endDay, Integer endMonth,
                 Date effBeginDt, Date effEndDt) {
        setId(id);
        setTinwsysIsNumber(tinwsysIsNumber);
        setNumber0(number0);
        setAlternateStNum(alternateStNum);
        setName(name);
        setActivityStatusCd(activityStatusCd);
        setDPrinCitySvdNm(dPrinCitySvdNm);
        setDPrinCntySvdNm(dPrinCntySvdNm);
        setDPopulationCount(dPopulationCount);
        setDPwsStTypeCd(dPwsStTypeCd);
        setActivityRsnTxt(activityRsnTxt);
        setStartDay(startDay);
        setStartMonth(startMonth);
        setEndDay(endDay);
        setEndMonth(endMonth);
        setEffBeginDt(DataBaseUtil.toYD(effBeginDt));
        setEffEndDt(DataBaseUtil.toYD(effEndDt));
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

    public String getNumber0() {
        return number0;
    }

    public void setNumber0(String number0) {
        this.number0 = DataBaseUtil.trim(number0);
        _changed = true;
    }

    public String getAlternateStNum() {
        return alternateStNum;
    }

    public void setAlternateStNum(String alternateStNum) {
        this.alternateStNum = DataBaseUtil.trim(alternateStNum);
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getActivityStatusCd() {
        return activityStatusCd;
    }

    public void setActivityStatusCd(String activityStatusCd) {
        this.activityStatusCd = DataBaseUtil.trim(activityStatusCd);
        _changed = true;
    }

    public String getDPrinCitySvdNm() {
        return dPrinCitySvdNm;
    }

    public void setDPrinCitySvdNm(String prinCitySvdNm) {
        dPrinCitySvdNm = DataBaseUtil.trim(prinCitySvdNm);
        _changed = true;
    }

    public String getDPrinCntySvdNm() {
        return dPrinCntySvdNm;
    }

    public void setDPrinCntySvdNm(String prinCntySvdNm) {
        dPrinCntySvdNm = DataBaseUtil.trim(prinCntySvdNm);
        _changed = true;
    }

    public Integer getDPopulationCount() {
        return dPopulationCount;
    }

    public void setDPopulationCount(Integer populationCount) {
        this.dPopulationCount = populationCount;
        _changed = true;
    }

    public String getDPwsStTypeCd() {
        return dPwsStTypeCd;
    }

    public void setDPwsStTypeCd(String pwsStTypeCd) {
        this.dPwsStTypeCd = DataBaseUtil.trim(pwsStTypeCd);
        _changed = true;
    }

    public String getActivityRsnTxt() {
        return activityRsnTxt;
    }

    public void setActivityRsnTxt(String activityRsnTxt) {
        this.activityRsnTxt = DataBaseUtil.trim(activityRsnTxt);
        _changed = true;
    }

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
        _changed = true;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
        _changed = true;
    }

    public Integer getEndDay() {
        return endDay;
    }

    public void setEndDay(Integer endDay) {
        this.endDay = endDay;
        _changed = true;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
        _changed = true;
    }

    public Datetime getEffBeginDt() {
        return effBeginDt;
    }

    public void setEffBeginDt(Datetime effBeginDt) {
        this.effBeginDt = DataBaseUtil.toYD(effBeginDt);
        _changed = true;
    }

    public Datetime getEffEndDt() {
        return effEndDt;
    }

    public void setEffEndDt(Datetime effEndDt) {
        this.effEndDt = DataBaseUtil.toYD(effEndDt);
        _changed = true;
    }
}
