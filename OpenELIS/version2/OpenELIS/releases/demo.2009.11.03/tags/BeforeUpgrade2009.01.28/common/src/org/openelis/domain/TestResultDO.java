/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import java.io.Serializable;

public class TestResultDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8348989200199215217L;
    
    protected Integer id;             

    protected Integer testId;             

    protected Integer resultGroup;             

    protected Integer sortOrder;
    
    protected Integer flagsId;             

    protected Integer typeId;             

    protected String value;             

    protected Integer significantDigits;             

    protected Integer roundingMethodId;
    
    protected String quantLimit;             
    
    protected String contLevel;      
   
    protected String hazardLevel;
    
    protected String dictEntry;
    
    private boolean delete = false;
    
    public TestResultDO() {
        
    }
    
    public TestResultDO(Integer id,Integer testId,Integer resultGroup,
                        Integer sortOrder,Integer flagsId,Integer typeId,String value,
                        Integer significantDigits,Integer roundingMethodId,
                        String quantLimit,String contLevel,String hazardLevel) {
        
        this.id = id;
        this.testId = testId;
        this.resultGroup = resultGroup;
        this.sortOrder = sortOrder;
        this.flagsId = flagsId;
        this.typeId = typeId;
        this.value = value;
        this.significantDigits = significantDigits;
        this.roundingMethodId = roundingMethodId;
        this.quantLimit = quantLimit;
        this.contLevel = contLevel;
        this.hazardLevel = hazardLevel;        
    }
    
    public String getContLevel() {
        return contLevel;
    }

    public void setContLevel(String contLevel) {
        this.contLevel = contLevel;
    }

    public Integer getFlagsId() {
        return flagsId;
    }

    public void setFlagsId(Integer flagsId) {
        this.flagsId = flagsId;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuantLimit() {
        return quantLimit;
    }

    public void setQuantLimit(String quantLimit) {
        this.quantLimit = quantLimit;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }

    public Integer getRoundingMethodId() {
        return roundingMethodId;
    }

    public void setRoundingMethodId(Integer roundingMethodId) {
        this.roundingMethodId = roundingMethodId;
    }

    public Integer getSignificantDigits() {
        return significantDigits;
    }

    public void setSignificantDigits(Integer significantDigits) {
        this.significantDigits = significantDigits;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getDictEntry() {
        return dictEntry;
    }

    public void setDictEntry(String dictEntry) {
        this.dictEntry = dictEntry;
    }

}
