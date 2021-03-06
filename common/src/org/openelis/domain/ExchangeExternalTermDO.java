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

import org.openelis.ui.common.DataBaseUtil;

/**
 * Class represents the fields in database table exchange_external_term
 */

public class ExchangeExternalTermDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, exchangeLocalTermId, profileId;
    protected String          isActive, externalTerm, externalDescription, 
                               externalCodingSystem, version;
    
    public ExchangeExternalTermDO() {
    }
    
    public ExchangeExternalTermDO(Integer id, Integer exchangeLocalTermId, Integer profileId,
                                  String isActive, String externalTerm, String externalDescription,
                                  String externalCodingSystem, String version) {
        setId(id);
        setExchangeLocalTermId(exchangeLocalTermId);
        setProfileId(profileId);
        setIsActive(isActive);
        setExternalTerm(externalTerm);
        setExternalDescription(externalDescription);
        setExternalCodingSystem(externalCodingSystem);
        setVersion(version);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getExchangeLocalTermId() {
        return exchangeLocalTermId;
    }

    public void setExchangeLocalTermId(Integer exchangeLocalTermId) {
        this.exchangeLocalTermId = exchangeLocalTermId;
        _changed = true;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public String getExternalTerm() {
        return externalTerm;
    }

    public void setExternalTerm(String externalTerm) {
        this.externalTerm = DataBaseUtil.trim(externalTerm);
        _changed = true;
    }

    public String getExternalDescription() {
        return externalDescription;
    }

    public void setExternalDescription(String externalDescription) {
        this.externalDescription = DataBaseUtil.trim(externalDescription);
        _changed = true;
    }

    public String getExternalCodingSystem() {
        return externalCodingSystem;
    }

    public void setExternalCodingSystem(String externalCodingSystem) {
        this.externalCodingSystem = DataBaseUtil.trim(externalCodingSystem);
        _changed = true;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = DataBaseUtil.trim(version);
        _changed = true;
    }
}