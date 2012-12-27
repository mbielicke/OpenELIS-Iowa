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
import java.util.ArrayList;

import org.openelis.domain.OrganizationContactDO;

public class OrganizationContactManager implements Serializable {

    private static final long                                  serialVersionUID = 1L;
    
    protected Integer                                          organizationId;
    protected ArrayList<OrganizationContactDO>                 contacts, deleted;

    protected transient static OrganizationContactManagerProxy proxy;

    protected OrganizationContactManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static OrganizationContactManager getInstance() {
        return new OrganizationContactManager();
    }

    public OrganizationContactDO getContactAt(int i) {
        return contacts.get(i);
    }

    public void setContactAt(OrganizationContactDO contact, int i) {
        if (contacts == null)
            contacts = new ArrayList<OrganizationContactDO>();
        contacts.set(i, contact);
    }

    public void addContact(OrganizationContactDO contact) {
        if (contacts == null)
            contacts = new ArrayList<OrganizationContactDO>();
        contacts.add(contact);
    }

    public void addContactAt(OrganizationContactDO contact, int i) {
        if (contacts == null)
            contacts = new ArrayList<OrganizationContactDO>();
        contacts.add(i, contact);
    }

    public void removeContactAt(int i) {
        OrganizationContactDO tmp;

        if (contacts == null || i >= contacts.size())
            return;

        tmp = contacts.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<OrganizationContactDO>();
            deleted.add(tmp);
        }
    }

    public int count() {
        if (contacts == null)
            return 0;
    
        return contacts.size();
    }

    // service methods
    public static OrganizationContactManager fetchByOrganizationId(Integer id) throws Exception {
        return proxy().fetchByOrganizationId(id);
    }

    public OrganizationContactManager add() throws Exception {
        return proxy().add(this);
    }

    public OrganizationContactManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
       // proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getOrganizationId() {
        return organizationId;
    }
    
    void setOrganizationId(Integer id) {
        organizationId = id;
    }

    ArrayList<OrganizationContactDO> getContacts() {
        return contacts;
    }

    void setContacts(ArrayList<OrganizationContactDO> contacts) {
        this.contacts = contacts;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    OrganizationContactDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static OrganizationContactManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrganizationContactManagerProxy();
        return proxy;
    }
}
