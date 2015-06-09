package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;

/**
 * This class is used to bulk load organization manager.
 */
public class OrganizationManager1Accessor {
    /**
     * Set/get objects from organization manager
     */
    public static OrganizationViewDO getOrganization(OrganizationManager1 om) {
        return om.organization;
    }

    public static void setOrganization(OrganizationManager1 om, OrganizationViewDO organization) {
        om.organization = organization;
    }

    public static ArrayList<OrganizationContactDO> getContacts(OrganizationManager1 om) {
        return om.contacts;
    }

    public static void setContacts(OrganizationManager1 om,
                                   ArrayList<OrganizationContactDO> contacts) {
        om.contacts = contacts;
    }

    public static void addContact(OrganizationManager1 om, OrganizationContactDO contact) {
        if (om.contacts == null)
            om.contacts = new ArrayList<OrganizationContactDO>();
        om.contacts.add(contact);
    }

    public static ArrayList<OrganizationParameterDO> getParameters(OrganizationManager1 om) {
        return om.parameters;
    }

    public static void setParameters(OrganizationManager1 om,
                                     ArrayList<OrganizationParameterDO> parameters) {
        om.parameters = parameters;
    }

    public static void addParameter(OrganizationManager1 om, OrganizationParameterDO parameter) {
        if (om.parameters == null)
            om.parameters = new ArrayList<OrganizationParameterDO>();
        om.parameters.add(parameter);
    }

    public static ArrayList<NoteViewDO> getNotes(OrganizationManager1 om) {
        return om.notes;
    }

    public static void setNotes(OrganizationManager1 om, ArrayList<NoteViewDO> notes) {
        om.notes = notes;
    }

    public static void addNote(OrganizationManager1 om, NoteViewDO note) {
        if (om.notes == null)
            om.notes = new ArrayList<NoteViewDO>();
        om.notes.add(note);
    }

    public static ArrayList<DataObject> getRemoved(OrganizationManager1 om) {
        return om.removed;
    }

    public static void setRemoved(OrganizationManager1 om, ArrayList<DataObject> removed) {
        om.removed = removed;
    }
}
