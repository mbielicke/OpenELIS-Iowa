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

import java.io.Serializable;

import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.gwt.common.NotFoundException;

public class ExchangeLocalTermManager implements Serializable {

    private static final long                                serialVersionUID = 1L;

    protected ExchangeLocalTermViewDO                        exchangeLocalTerm;
    protected ExchangeExternalTermManager                    externalTerms;

    protected transient static ExchangeLocalTermManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the static methods for allocation.
     */
    protected ExchangeLocalTermManager() {
        exchangeLocalTerm = null;
        externalTerms = null;
    }
    
    /**
     * Creates a new instance of this object. A default organization object is
     * also created.
     */
    public static ExchangeLocalTermManager getInstance() {
        ExchangeLocalTermManager manager;
        
        manager = new ExchangeLocalTermManager();
        manager.exchangeLocalTerm = new ExchangeLocalTermViewDO();
        
        return manager;
    }
    
    public ExchangeLocalTermViewDO getExchangeLocalTerm() {
        return exchangeLocalTerm;
    }

    public void setExchangeLocalTerm(ExchangeLocalTermViewDO exchangeLocalTerm) {
        this.exchangeLocalTerm = exchangeLocalTerm;
    }

    public static ExchangeLocalTermManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static ExchangeLocalTermManager fetchWithExternalTerms(Integer id) throws Exception {
        return proxy().fetchWithExternalTerms(id);
    }

    public ExchangeLocalTermManager add() throws Exception {
        return proxy().add(this);
    }

    public ExchangeLocalTermManager update() throws Exception {
        return proxy().update(this);
    }
    
    public ExchangeLocalTermManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(exchangeLocalTerm.getId());
    }
    
    public ExchangeLocalTermManager abortUpdate() throws Exception {
        return proxy().abortUpdate(exchangeLocalTerm.getId());
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public ExchangeExternalTermManager getExternalTerms() throws Exception {
        if (externalTerms == null) {
            if (exchangeLocalTerm.getId() != null) {
                try {
                    externalTerms = ExchangeExternalTermManager.fetchByExchangeLocalTermId(exchangeLocalTerm.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (externalTerms == null)
                externalTerms = ExchangeExternalTermManager.getInstance();
        }
        return externalTerms;
    }

    void setExternalTerms(ExchangeExternalTermManager externalTerms) {
        this.externalTerms = externalTerms;
    }
    
    private static ExchangeLocalTermManagerProxy proxy() {
        if (proxy == null)
            proxy = new ExchangeLocalTermManagerProxy();

        return proxy;
    }
}