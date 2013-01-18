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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.ExchangeProfileDO;
import org.openelis.gwt.common.RPC;

public class ExchangeProfileManager implements RPC {

    private static final long                              serialVersionUID = 1L;
    
    protected Integer                                      exchangeCriteriaId;
    protected ArrayList<ExchangeProfileDO>                 profiles, deleted;

    protected transient static ExchangeProfileManagerProxy proxy;

    protected ExchangeProfileManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static ExchangeProfileManager getInstance() {
        return new ExchangeProfileManager();
    }

    public ExchangeProfileDO getProfileAt(int i) {
        return profiles.get(i);
    }

    public void setProfileAt(ExchangeProfileDO profile, int i) {
        if (profiles == null)
            profiles = new ArrayList<ExchangeProfileDO>();
        profiles.set(i, profile);
    }

    public void addProfile(ExchangeProfileDO profile) {
        if (profiles == null)
            profiles = new ArrayList<ExchangeProfileDO>();
        profiles.add(profile);
    }

    public void addProfileAt(ExchangeProfileDO profile, int i) {
        if (profiles == null)
            profiles = new ArrayList<ExchangeProfileDO>();
        profiles.add(i, profile);
    }

    public void removeProfileAt(int i) {
        ExchangeProfileDO tmp;

        if (profiles == null || i >= profiles.size())
            return;

        tmp = profiles.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<ExchangeProfileDO>();
            deleted.add(tmp);
        }
    }
    
    public void moveProfile(int oldIndex, int newIndex) {
        ExchangeProfileDO profile;

        if (profiles == null)
            return;

        profile = profiles.remove(oldIndex);
        if (newIndex > oldIndex)
            newIndex-- ;

        if (newIndex >= count())
            addProfile(profile);
        else
            addProfileAt(profile, newIndex);
    }

    public int count() {
        if (profiles == null)
            return 0;
    
        return profiles.size();
    }

    // service methods
    public static ExchangeProfileManager fetchByExchangeCriteriaId(Integer id) throws Exception {
        return proxy().fetchByExchangeCriteriaId(id);
    }

    public ExchangeProfileManager add() throws Exception {
        return proxy().add(this);
    }

    public ExchangeProfileManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void delete() throws Exception {
        proxy().delete(this);            
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getExchangeCriteriaId() {
        return exchangeCriteriaId;
    }
    
    void setExchangeCriteriaId(Integer id) {
        exchangeCriteriaId = id;
    }

    ArrayList<ExchangeProfileDO> getProfiles() {
        return profiles;
    }

    void setProfiles(ArrayList<ExchangeProfileDO> profiles) {
        this.profiles = profiles;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    ExchangeProfileDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static ExchangeProfileManagerProxy proxy() {
        if (proxy == null)
            proxy = new ExchangeProfileManagerProxy();
        return proxy;
    }
}
