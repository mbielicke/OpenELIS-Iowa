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

import org.openelis.utilcommon.DataBaseUtil;

public class QcAnalyteDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;             
    protected Integer qcId;             
    protected Integer analyteId;   
    protected String analyteName;
    protected Integer typeId;             
    protected String value;            
    protected String isTrendable;     
    
    private boolean delete;
    
    public QcAnalyteDO() {
        
    }
    
    public QcAnalyteDO(Integer id,Integer qcId,Integer analyteId,String analyteName,
                       Integer typeId,String value,String isTrendable) {
        setId(id);
        setQcId(qcId);
        setAnalyteId(analyteId);
        setAnalyteName(analyteName);
        setTypeId(typeId);
        setValue(value);
        setIsTrendable(isTrendable);        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer qcId) {
        this.qcId = qcId;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
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
        this.value = DataBaseUtil.trim(value);
    }

    public String getIsTrendable() {
        return isTrendable;
    }

    public void setIsTrendable(String isTrendable) {
        this.isTrendable = DataBaseUtil.trim(isTrendable);
    }

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = analyteName;
    }
   

}
