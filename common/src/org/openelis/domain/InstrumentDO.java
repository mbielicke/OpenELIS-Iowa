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

import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;



public class InstrumentDO implements RPC {

    private static final long serialVersionUID = 1L;
    
    private Integer id;             
    private String name;             
    private String description;             
    private String modelNumber;             
    private String serialNumber;             
    private Integer typeId;             
    private String location;             
    private String isActive;             
    private Datetime activeBegin;             
    private Datetime activeEnd;             
    private Integer scriptletId;
    private String scriptletName;
    
    public InstrumentDO() {
        
    }
    
    public InstrumentDO(Integer id,String name,String description,String modelNumber,
                        String serialNumber,Integer typeId,String location,
                        String isActive,Date activeBegin,Date activeEnd,
                        Integer scriptletId,String scriptletName) {
        setId(id);
        setName(name);
        setDescription(description);
        setModelNumber(modelNumber);
        setSerialNumber(serialNumber);
        setTypeId(typeId);
        setLocation(location);
        setIsActive(isActive);
        setActiveBegin(activeBegin);
        setActiveEnd(activeEnd);
        setScriptletId(scriptletId);
        setScriptletName(scriptletName);        
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getModelNumber() {
        return modelNumber;
    }
    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public Integer getTypeId() {
        return typeId;
    }
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getIsActive() {
        return isActive;
    }
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
    public Datetime getActiveBegin() {
        return activeBegin;
    }
    public void setActiveBegin(Date activeBegin) {
        this.activeBegin = new Datetime(Datetime.YEAR, Datetime.DAY,activeBegin);
    }
    public Datetime getActiveEnd() {
        return activeEnd;
    }
    public void setActiveEnd(Date activeEnd) {
        this.activeEnd = new Datetime(Datetime.YEAR, Datetime.DAY,activeEnd);
    }
    public Integer getScriptletId() {
        return scriptletId;
    }
    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
    }
    public String getScriptletName() {
        return scriptletName;
    }
    public void setScriptletName(String scriptletName) {
        this.scriptletName = scriptletName;
    }    
    

}
