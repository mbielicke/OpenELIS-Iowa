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


public class TestWorksheetItemDO implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 298904701466586041L;
    
    protected Integer id;             

    protected Integer testWorksheetId;             

    protected Integer position;             

    protected Integer typeId;             

    protected String qcName;
    
    private boolean delete = false;
    
    public TestWorksheetItemDO() {
        
    }
    
    public TestWorksheetItemDO(Integer id,Integer testWorksheetId,
                               Integer position, Integer typeId,
                               String qcName) {
        this.id = id;
        this.testWorksheetId = testWorksheetId;
        this.position = position;
        this.typeId = typeId;
        this.qcName = qcName;
    }

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public Integer getTestWorksheetId() {
        return testWorksheetId;
    }

    public void setTestWorksheetId(Integer testWorksheetId) {
        this.testWorksheetId = testWorksheetId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

}
