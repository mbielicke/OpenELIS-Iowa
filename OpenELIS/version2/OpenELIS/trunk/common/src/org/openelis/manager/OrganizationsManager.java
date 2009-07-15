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

import org.openelis.domain.OrganizationAddressDO;
import org.openelis.gwt.common.RPC;
import org.openelis.manager.NotesManager;
import org.openelis.manager.OrganizationContactsManager;
import org.openelis.manager.proxy.OrganizationsManagerProxy;

public class OrganizationsManager implements RPC {

    private static final long                           serialVersionUID = 1L;
    protected OrganizationContactsManager               contacts;
    protected NotesManager                              notes;
    protected OrganizationAddressDO                     organizationAddress;
    protected Integer organizationReferenceTable;
    public boolean load = false;
    public boolean cached = false;
    
    protected transient static OrganizationsManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the three static methods for allocation.
     */
    protected OrganizationsManager() {
        contacts = null;
        notes = null;
        organizationAddress = null;
    }

    /**
     * Creates a new instance of this object. A default organization object is also created.
     */
    public static OrganizationsManager getInstance() {
        OrganizationsManager om;

        om = new OrganizationsManager();
        om.organizationAddress = new OrganizationAddressDO();

        return om;
    }
    
    public static OrganizationsManager findById(Integer id) throws Exception {
        return proxy().fetch(id);
    }
    
    /*
    public static OrganizationsManager findByIdWithContacts(Integer id){
        return io().fe
    }
    
    public static OrganizationsManager findByIdWithNotes(Integer id){
        
    }
    
    public static OrganizationsManager findByIdWithIdentifiers(Integer id){
        
    }
    */
    
    //getters/setters
    public void setNotesManager(NotesManager notesManager){
        notes = notesManager;
    }
    
    public NotesManager getNotesManager(){
        if (notes == null) 
            notes = NotesManager.getInstance();
        
        if(notes.getReferenceId() == null){
            notes.setExternal(false);
            notes.setReferenceId(organizationAddress.getOrganizationId());
            notes.setReferenceTableId(organizationReferenceTable);
        }

        return notes;
    }
    
    public void setContactsManager(OrganizationContactsManager contactsManager){
        contacts = contactsManager;
    }
    
    public OrganizationContactsManager getContactsManager(){
        if (contacts == null) 
            contacts = OrganizationContactsManager.getInstance();
        
        if(organizationAddress.getOrganizationId() == null)
            contacts.setOrganizationId(organizationAddress.getOrganizationId());

        return contacts;
    }
    
    public OrganizationAddressDO getOrganizationAddress() {
        return organizationAddress;
    }

    public void setOrganizationAddress(OrganizationAddressDO organizationAddress) {
        this.organizationAddress = organizationAddress;
        cached = true;
    }
    
    public Integer getOrganizationReferenceTable() {
        return organizationReferenceTable;
    }

    public void setOrganizationReferenceTable(Integer organizationReferenceTable) {
        this.organizationReferenceTable = organizationReferenceTable;
    }
    
    //service methods
    public OrganizationsManager add() throws Exception {
        return proxy().add(this);
        
    }
    
    public OrganizationsManager update() throws Exception {
        return proxy().update(this);
        
    }
    
    public OrganizationsManager fetchForUpdate(Integer id) throws Exception {
        organizationAddress.setOrganizationId(id);
        return proxy().fetchForUpdate(id);
        
    }
    
    public OrganizationsManager abort() throws Exception {
        return proxy().abort(this);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    private static OrganizationsManagerProxy proxy(){
        if(proxy == null)
            proxy = new OrganizationsManagerProxy();
        
        return proxy;
    }
}
