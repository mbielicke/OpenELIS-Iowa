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
package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.gwt.common.NotFoundException;

public class ExchangeCriteriaManager implements Serializable {

    private static final long                               serialVersionUID = 1L;

    protected ExchangeCriteriaViewDO                        exchangeCriteria;
    protected ExchangeProfileManager                        profiles;

    protected transient static ExchangeCriteriaManagerProxy proxy;

    /**
     * This is a protected constructor. See the static methods for allocation.
     */
    protected ExchangeCriteriaManager() {
        profiles = null;
        exchangeCriteria = null;
    }

    /**
     * Creates a new instance of this object. A default ExchangeCriteria object is
     * also created.
     */
    public static ExchangeCriteriaManager getInstance() {
        ExchangeCriteriaManager manager;

        manager = new ExchangeCriteriaManager();
        manager.exchangeCriteria = new ExchangeCriteriaViewDO();

        return manager;
    }

    public ExchangeCriteriaViewDO getExchangeCriteria() {
        return exchangeCriteria;
    }

    public void setExchangeCriteria(ExchangeCriteriaViewDO exchangeCriteria) {
        this.exchangeCriteria = exchangeCriteria;
    }

    // service methods
    public static ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }
    
    public static ExchangeCriteriaManager fetchByName(String name) throws Exception {
        return proxy().fetchByName(name);
    }

    public static ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        return proxy().fetchWithProfiles(id);
    }
    
    public static ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        return proxy().fetchWithProfilesByName(name);
    }

    public ExchangeCriteriaManager add() throws Exception {
        return proxy().add(this);
    }

    public ExchangeCriteriaManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void delete() throws Exception {
        proxy().delete(this);
    }

    public ExchangeCriteriaManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(exchangeCriteria.getId());
    }

    public ExchangeCriteriaManager abortUpdate() throws Exception {
        return proxy().abortUpdate(exchangeCriteria.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public ExchangeProfileManager getProfiles() throws Exception {
        if (profiles == null) {
            if (exchangeCriteria.getId() != null) {
                try {
                    profiles = ExchangeProfileManager.fetchByExchangeCriteriaId(exchangeCriteria.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (profiles == null)
                profiles = ExchangeProfileManager.getInstance();
        }
        return profiles;
    }
    
    private static ExchangeCriteriaManagerProxy proxy() {
        if (proxy == null)
            proxy = new ExchangeCriteriaManagerProxy();

        return proxy;
    }
}
