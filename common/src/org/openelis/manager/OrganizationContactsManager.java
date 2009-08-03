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

public class OrganizationContactsManager implements RPC {
    
    private static final long serialVersionUID = 1L;
    protected Integer                           organizationId;
    protected ArrayList<OrganizationContactDO>  contacts;
    protected ArrayList<OrganizationContactDO>  deletedContacts;
    
    protected transient static OrganizationContactsManagerProxy proxy;
    
    protected OrganizationContactsManager() {
        contacts = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static OrganizationContactsManager getInstance() {
        OrganizationContactsManager ocm;

        ocm = new OrganizationContactsManager();
        ocm.contacts = new ArrayList<OrganizationContactDO>();

        return ocm;
    }
    
    public static OrganizationContactsManager findByOrganizationId(Integer orgId) throws Exception {
        return proxy().fetchByOrgId(orgId);
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
    
    public int count(){
        if(contacts == null)
            return 0;
        
        return contacts.size();
    }
    
    public OrganizationContactDO getContactAt(int i) {
        return contacts.get(i);
    }
    
    public void setContactAt(OrganizationContactDO contact, int i) {
        contacts.set(i, contact);
    }
    
    public void addContact(OrganizationContactDO contact){
        if(contacts == null)
            contacts = new ArrayList<OrganizationContactDO>();
        
        contacts.add(contact);
    }
    
    public void addContactAt(OrganizationContactDO contact, int i){
        if(contacts == null)
            contacts = new ArrayList<OrganizationContactDO>();
        
        contacts.add(i, contact);
    }
    
    public void removeContactAt(int i){
        if(contacts == null || i >= contacts.size())
            return;
        
        OrganizationContactDO tmpDO = contacts.remove(i);
        
        if(deletedContacts == null)
            deletedContacts = new ArrayList<OrganizationContactDO>();
        
        deletedContacts.add(tmpDO);
    }
    
    //service methods
    public OrganizationContactsManager add() throws Exception {
        return proxy().add(this);
    }
    
    public OrganizationContactsManager update() throws Exception {
        return proxy().update(this);
    }
       
    private static OrganizationContactsManagerProxy proxy(){
        if(proxy == null)
            proxy = new OrganizationContactsManagerProxy();
        
        return proxy;
    }

    //these are friendly methods so only managers and proxies can call this method
    ArrayList<OrganizationContactDO> getContacts() {
        return contacts;
    }

    void setContacts(ArrayList<OrganizationContactDO> contacts) {
        this.contacts = contacts;
    }
    
    int deleteCount(){
        if(deletedContacts == null)
            return 0;
        
        return deletedContacts.size();
    }
    
    OrganizationContactDO getDeletedAt(int i){
        return deletedContacts.get(i);
    }
}
