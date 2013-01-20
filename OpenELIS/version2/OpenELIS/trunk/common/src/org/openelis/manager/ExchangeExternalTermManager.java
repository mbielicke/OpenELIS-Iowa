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
import java.util.ArrayList;

import org.openelis.domain.ExchangeExternalTermDO;

public class ExchangeExternalTermManager implements Serializable {

    private static final long                                   serialVersionUID = 1L;
    
    protected Integer                                           exchangeLocalTermId;
    protected ArrayList<ExchangeExternalTermDO>                 externalTerms, deleted;

    protected transient static ExchangeExternalTermManagerProxy proxy;
    
    protected ExchangeExternalTermManager () {        
    }
    
    public static ExchangeExternalTermManager getInstance() {
        return new ExchangeExternalTermManager();
    }
    
    public ExchangeExternalTermDO getExternalTermAt(int i) {
        return externalTerms.get(i);
    }

    public void setExternalTermAt(ExchangeExternalTermDO externalTerm, int i) {
        if (externalTerms == null)
            externalTerms = new ArrayList<ExchangeExternalTermDO>();
        externalTerms.set(i, externalTerm);
    }

    public void addExternalTerm(ExchangeExternalTermDO externalTerm) {
        if (externalTerms == null)
            externalTerms = new ArrayList<ExchangeExternalTermDO>();
        externalTerms.add(externalTerm);
    }

    public void addExternalTermAt(ExchangeExternalTermDO externalTerm, int i) {
        if (externalTerms == null)
            externalTerms = new ArrayList<ExchangeExternalTermDO>();
        externalTerms.add(i, externalTerm);
    }

    public void removeExternalTermAt(int i) {
        ExchangeExternalTermDO externalTerm;

        if (externalTerms == null || i >= externalTerms.size())
            return;

        externalTerm = externalTerms.remove(i);
        if (externalTerm.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<ExchangeExternalTermDO>();        
            deleted.add(externalTerm);
        }
    }
    
    public int count() {
        if (externalTerms == null)
            return 0;
        
        return externalTerms.size();
    }
    
    // service methods 
    public static ExchangeExternalTermManager fetchByExchangeLocalTermId(Integer id) throws Exception {
        return proxy().fetchByExchangeLocalTermId(id);
    }
    
    public ExchangeExternalTermManager add() throws Exception {
        return proxy().add(this);
    }

    public ExchangeExternalTermManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getExchangeLocalTermId() {
        return exchangeLocalTermId;
    }
    
    void setExchangeLocalTermId(Integer id) {
        exchangeLocalTermId = id;
    }

    ArrayList<ExchangeExternalTermDO> getExternalTerms() {
        return externalTerms;
    }

    void setExternalTerms(ArrayList<ExchangeExternalTermDO> externalTerms) {
        this.externalTerms = externalTerms;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    ExchangeExternalTermDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static ExchangeExternalTermManagerProxy proxy() {
        if (proxy == null)
            proxy = new ExchangeExternalTermManagerProxy();
        return proxy;
    }
}