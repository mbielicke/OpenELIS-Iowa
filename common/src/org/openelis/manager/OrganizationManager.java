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

import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class OrganizationManager implements RPC, HasNotesInt {

    private static final long                           serialVersionUID = 1L;

    protected OrganizationViewDO                        organization;
    protected OrganizationContactManager                contacts;
    protected NoteManager                               notes;

    protected transient static OrganizationManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected OrganizationManager() {
        contacts = null;
        notes = null;
        organization = null;
    }

    /**
     * Creates a new instance of this object. A default organization object is
     * also created.
     */
    public static OrganizationManager getInstance() {
        OrganizationManager manager;

        manager = new OrganizationManager();
        manager.organization = new OrganizationViewDO();

        return manager;
    }

    public OrganizationViewDO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationViewDO organization) {
        this.organization = organization;
    }

    // service methods
    public static OrganizationManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static OrganizationManager fetchWithContacts(Integer id) throws Exception {
        return proxy().fetchWithContacts(id);
    }

    public static OrganizationManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }

    public static OrganizationManager fetchWithIdentifiers(Integer id) throws Exception {
        return proxy().fetchWithIdentifiers(id);
    }

    public OrganizationManager add() throws Exception {
        return proxy().add(this);
    }

    public OrganizationManager update() throws Exception {
        return proxy().update(this);
    }

    public OrganizationManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(organization.getId());
    }

    public OrganizationManager abortUpdate() throws Exception {
        return proxy().abortUpdate(organization.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public NoteManager getNotes() throws Exception {
        
        if (notes == null) {
            if (organization.getId() != null) {
                try {
                    notes = NoteManager.findByRefTableRefId(ReferenceTable.ORGANIZATION, organization.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (notes == null)
                notes = NoteManager.getInstance();
        }
        return notes;
    }

    public OrganizationContactManager getContacts() throws Exception {
        if (contacts == null) {
            if (organization.getId() != null) {
                try {
                    contacts = OrganizationContactManager.fetchByOrganizationId(organization.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (contacts == null)
                contacts = OrganizationContactManager.getInstance();
        }
        return contacts;
    }

    private static OrganizationManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrganizationManagerProxy();

        return proxy;
    }
}
