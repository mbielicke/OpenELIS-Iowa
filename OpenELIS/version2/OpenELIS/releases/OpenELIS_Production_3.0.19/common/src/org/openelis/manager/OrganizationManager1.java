package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.common.NotFoundException;

public class OrganizationManager1 implements Serializable, HasNotesInt {
    private static final long serialVersionUID = 1L;

    /**
     * Flags that specifies what optional data to load with the manager
     */
    public enum Load {
        CONTACTS, PARAMETERS, NOTES
    };

    protected OrganizationViewDO                 organization;
    protected ArrayList<OrganizationContactDO>   contacts;
    protected ArrayList<OrganizationParameterDO> parameters;
    protected ArrayList<NoteViewDO>              notes;
    protected ArrayList<DataObject>              removed;

    transient public final OrganizationContact   contact   = new OrganizationContact();
    transient public final OrganizationParameter parameter = new OrganizationParameter();
    transient public final Note                  note      = new Note();

    /**
     * Initialize an empty organization manager
     */
    public OrganizationManager1() {
        organization = new OrganizationViewDO();
    }

    /**
     * Returns the organization view DO
     */
    public OrganizationViewDO getOrganization() {
        return organization;
    }

    /**
     * Class to manage contacts
     */
    public class OrganizationContact {

        /**
         * Returns the organization's contact at specified index.
         */
        public OrganizationContactDO get(int i) {
            return contacts.get(i);
        }

        public OrganizationContactDO add() {
            OrganizationContactDO data;

            data = new OrganizationContactDO();
            if (contacts == null)
                contacts = new ArrayList<OrganizationContactDO>();
            contacts.add(data);
            return data;
        }

        public void remove(int i) {
            OrganizationContactDO data;

            data = contacts.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(OrganizationContactDO data) {
            contacts.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of contacts
         */
        public int count() {
            return (contacts == null) ? 0 : contacts.size();
        }
    }

    /**
     * Class to manage parameters
     */
    public class OrganizationParameter {
        /**
         * Returns the organization's parameter at specified index.
         */
        public OrganizationParameterDO get(int i) {
            return parameters.get(i);
        }

        public OrganizationParameterDO add() {
            OrganizationParameterDO data;

            data = new OrganizationParameterDO();
            if (parameters == null)
                parameters = new ArrayList<OrganizationParameterDO>();
            parameters.add(data);
            return data;
        }

        public void remove(int i) {
            OrganizationParameterDO data;

            data = parameters.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(OrganizationParameterDO data) {
            parameters.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of parameters
         */
        public int count() {
            return (parameters == null) ? 0 : parameters.size();
        }
    }

    /**
     * Class to manage notes
     */
    public class Note {

        /**
         * Returns the organization's note at specified index.
         */
        public NoteViewDO get(int i) {
            return notes.get(i);
        }

        /**
         * Returns the editing note. For internal, only the currently
         * uncommitted note can be edited. If no editing note currently exists,
         * one is created and returned.
         */
        public NoteViewDO getEditing() {
            NoteViewDO data;

            if (notes == null)
                notes = new ArrayList<NoteViewDO>(1);

            if (notes.size() == 0 || (notes.get(0).getId() != null)) {
                data = new NoteViewDO();
                data.setIsExternal("N");
                notes.add(0, data);
            }

            return notes.get(0);
        }

        /**
         * Removes the editing note. For internal, only the uncommitted note is
         * removed.
         */
        public void removeEditing() {
            NoteViewDO data;

            if (notes != null && notes.size() > 0) {
                data = notes.get(0);
                if (data.getId() == null)
                    notes.remove(0);
            }
        }

        /**
         * Returns the number of internal note(s)
         */
        public int count() {
            return (notes == null) ? 0 : notes.size();
        }
    }

    /**
     * Adds the specified data object to the list of objects that should be
     * removed from the database.
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id != null)
            removed.add(data);
    }

    @Override
    public NoteManager getNotes() throws Exception {
        NoteManager noteMan;

        noteMan = null;
        if (notes == null) {
            if (organization.getId() != null) {
                try {
                    noteMan = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().ORGANIZATION,
                                                                    organization.getId(),
                                                                    false);
                    notes = noteMan.getNotes();
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (notes == null) {
                noteMan = NoteManager.getInstance();
                notes = noteMan.getNotes();
            }
        } else {
            noteMan = NoteManager.getInstance();
            noteMan.setNotes(notes);
        }
        return noteMan;
    }
}
