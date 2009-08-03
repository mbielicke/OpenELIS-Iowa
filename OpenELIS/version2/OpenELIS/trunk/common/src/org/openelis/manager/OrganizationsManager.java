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
import org.openelis.exception.NotFoundException;
import org.openelis.gwt.common.RPC;

public class OrganizationsManager implements RPC, HasNotesInt {

    private static final long                           serialVersionUID = 1L;
    protected OrganizationContactsManager               contacts;
    protected NotesManager                              notes;
    protected OrganizationAddressDO                     organizationAddress;
    protected Integer organizationReferenceTable;
    
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
    
    
    public static OrganizationsManager findByIdWithContacts(Integer id) throws Exception {
        return proxy().fetchWithContacts(id);
    }
    
    public static OrganizationsManager findByIdWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }
    
    public static OrganizationsManager findByIdWithIdentifiers(Integer id) throws Exception { 
        return proxy().fetchWithIdentifiers(id);
    }
    
    public OrganizationsManager fetchForUpdate() throws Exception {
        if(organizationAddress.getOrganizationId() == null)
            throw new Exception("org id is null");
        
        return proxy().fetchForUpdate(organizationAddress.getOrganizationId());
    }
    
    //getters/setters
    public NotesManager getNotes() throws Exception {
        if(notes == null){
            if(organizationAddress.getOrganizationId() != null){
                try{
                    notes = NotesManager.findByRefTableRefId(organizationReferenceTable, organizationAddress.getOrganizationId());
                    
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
        
        if(notes == null){
            notes = NotesManager.getInstance();
        }

        return notes;
    }  
    public OrganizationContactsManager getContacts() throws Exception {
        if(contacts == null){
            if(organizationAddress.getOrganizationId() != null){
                try{
                    contacts = OrganizationContactsManager.findByOrganizationId(organizationAddress.getOrganizationId());
                    
                }
                catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
         if(contacts == null)
            contacts = OrganizationContactsManager.getInstance();
     
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
    public OrganizationsManager add() throws Exception {
        return proxy().add(this);
        
    }
    
    public OrganizationsManager update() throws Exception {
        return proxy().update(this);
        
    }
    
    public OrganizationsManager abort() throws Exception {
        return proxy().abort(organizationAddress.getOrganizationId());
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
