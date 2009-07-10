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
package org.openelis.managerCommon;

import org.openelis.domain.OrganizationAddressDO;
import org.openelis.gwt.common.RPC;
import org.openelis.manager.io.OrganizationsManagerIO;

public class OrganizationsManager implements RPC {

    private static final long                           serialVersionUID = 1L;
    protected OrganizationContactsManager               contacts;
    protected NotesManager                              notes;
    protected OrganizationAddressDO                     organizationAddress;
    protected Integer organizationReferenceTable;
    public boolean load = false;
    public boolean cached = false;
    
    protected transient OrganizationsManagerIOInt io;
    
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
    
    //getters/setters
    public void setNotesManager(NotesManager notesManager){
        notes = notesManager;
    }
    
    public NotesManager getNotesManager(){
        if (notes == null) {
            notes = NotesManager.getInstance();
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
        if (contacts == null) {
            contacts = OrganizationContactsManager.getInstance();
            contacts.setOrganizationId(organizationAddress.getOrganizationId());
        }

        return contacts;
    }
    
    public OrganizationAddressDO getOrganizationAddress() {
        return organizationAddress;
    }

    public void setOrganizationAddress(OrganizationAddressDO organizationAddress) {
        this.organizationAddress = organizationAddress;
    }
    
    public Integer getOrganizationReferenceTable() {
        return organizationReferenceTable;
    }

    public void setOrganizationReferenceTable(Integer organizationReferenceTable) {
        this.organizationReferenceTable = organizationReferenceTable;
    }
    
    //service methods
    public OrganizationsManager add(){
        return manager().commitAdd(this);
        
    }
    
    public OrganizationsManager update(){
        return manager().commitUpdate(this);
        
    }
    
    public OrganizationsManager fetch(Integer id){
        organizationAddress.setOrganizationId(id);
        return manager().fetch(this);
        
    }
    
    public OrganizationsManager fetchForUpdate(Integer id){
        organizationAddress.setOrganizationId(id);
        return manager().fetchForUpdate(this);
        
    }
    
    public OrganizationsManager abort(){
        return manager().abort(this);
    }
    
    private OrganizationsManagerIOInt manager(){
        if(io == null)
            io = new OrganizationsManagerIO();
        
        return io;
    }
}
