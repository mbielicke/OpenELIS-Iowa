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

import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.RPC;
import org.openelis.manager.proxy.OrganizationContactsManagerProxy;

public class OrganizationContactsManager implements RPC {
    
    private static final long serialVersionUID = 1L;
    protected Integer                           organizationId;
    protected ArrayList<OrganizationContactDO>  contacts;
    public boolean load = false;
    public boolean cached = false;
    
    protected transient OrganizationContactsManagerProxy proxy;
    
    /**
     * Creates a new instance of this object.
     */
    public static OrganizationContactsManager getInstance() {
        OrganizationContactsManager ocm;

        ocm = new OrganizationContactsManager();
        ocm.contacts = new ArrayList<OrganizationContactDO>();

        return ocm;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
    
    //service methods
    public OrganizationContactsManager add() throws Exception {
        return proxy().commitAdd(this);
    }
    
    public OrganizationContactsManager update() throws Exception {
        return proxy().commitUpdate(this);
    }
    
    public OrganizationContactsManager fetch() throws Exception {
        if (cached || !load)
            return this;

        cached = true;
        load = false;
        
        if(organizationId == null)
            return null;
        
        return proxy().fetch(this);
    }
    
    private OrganizationContactsManagerProxy proxy(){
        if(proxy == null)
            proxy = new OrganizationContactsManagerProxy();
        
        return proxy;
    }

    public ArrayList<OrganizationContactDO> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<OrganizationContactDO> contacts) {
        this.contacts = contacts;
    }
}
