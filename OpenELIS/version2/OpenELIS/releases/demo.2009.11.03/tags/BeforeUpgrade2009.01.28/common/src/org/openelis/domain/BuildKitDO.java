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
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class BuildKitDO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected Integer kitId;
    protected String kit;
    protected Integer numberRequested;
    protected boolean addToExisiting;
    protected Integer locationId;
    protected String location;
    protected String lotNumber;
    protected Datetime expDate;
    
    public BuildKitDO(){
        
    }

    public boolean isAddToExisiting() {
        return addToExisiting;
    }

    public void setAddToExisiting(boolean addToExisiting) {
        this.addToExisiting = addToExisiting;
    }

    public Datetime getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = new Datetime(Datetime.YEAR, Datetime.DAY, expDate);
    }

    public String getKit() {
        return kit;
    }

    public void setKit(String kit) {
        this.kit = DataBaseUtil.trim(kit);
    }

    public Integer getKitId() {
        return kitId;
    }

    public void setKitId(Integer kitId) {
        this.kitId = kitId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getNumberRequested() {
        return numberRequested;
    }

    public void setNumberRequested(Integer numberRequested) {
        this.numberRequested = numberRequested;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = DataBaseUtil.trim(lotNumber);
    }

}
