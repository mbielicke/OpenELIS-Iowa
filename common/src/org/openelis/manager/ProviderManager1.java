package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.DataObject;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ProviderAnalyteViewDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ProviderLocationDO;

/**
 * This class encapsulates a provider and all its related information including
 * locations, notes and analytes. Although the class provides some basic
 * functions internally, it is designed to interact with EJB methods to provide
 * majority of the operations needed to manage a provider.
 */
public class ProviderManager1 implements Serializable {
    private static final long                     serialVersionUID = 1L;

    protected ProviderDO                          provider;
    protected ArrayList<ProviderLocationDO>       locations;
    protected ArrayList<NoteViewDO>               notes;
    protected ArrayList<ProviderAnalyteViewDO>    analytes;
    protected ArrayList<DataObject>               removed;
    protected int                                 nextUID          = -1;

    transient public final ProviderLocation       location         = new ProviderLocation();
    transient public final Note                   note             = new Note();
    transient public final ProviderAnalyte        analyte          = new ProviderAnalyte();
    transient private HashMap<String, DataObject> uidMap;

    /**
     * Initialize an empty provider manager
     */
    public ProviderManager1() {
    }

    /**
     * Returns the provider DO
     */
    public ProviderDO getProvider() {
        return provider;
    }

    /**
     * Returns the next negative Id for this sample's newly created and as yet
     * uncommitted data objects e.g. order tests and order test analytes.
     */
    public int getNextUID() {
        return --nextUID;
    }

    /**
     * Returns the data object using its Uid.
     */
    public DataObject getObject(String uid) {
        if (uidMap == null) {
            uidMap = new HashMap<String, DataObject>();
        }
        return uidMap.get(uid);
    }

    /**
     * Class to manage Provider Location information
     */
    public class ProviderLocation {
        /**
         * Returns the location at specified index.
         */
        public ProviderLocationDO get(int i) {
            return locations.get(i);
        }

        public ProviderLocationDO add() {
            ProviderLocationDO data;

            data = new ProviderLocationDO();
            if (locations == null)
                locations = new ArrayList<ProviderLocationDO>();
            locations.add(data);

            return data;
        }

        /**
         * Removes a location from the list
         */
        public void remove(int i) {
            ProviderLocationDO data;

            data = locations.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(ProviderLocationDO data) {
            locations.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of locations associated with this provider
         */
        public int count() {
            if (locations != null)
                return locations.size();
            return 0;
        }

    }

    /**
     * Class to manage notes
     */
    public class Note {

        /**
         * Returns the provider's note at specified index.
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
         * Returns the number of note(s)
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

    /**
     * Class to manage Provider Analyte information
     */
    public class ProviderAnalyte {
        /**
         * Returns the analyte at specified index.
         */
        public ProviderAnalyteViewDO get(int i) {
            return analytes.get(i);
        }

        public ProviderAnalyteViewDO add() {
            ProviderAnalyteViewDO data;

            data = new ProviderAnalyteViewDO();
            if (analytes == null)
                analytes = new ArrayList<ProviderAnalyteViewDO>();
            analytes.add(data);

            return data;
        }

        /**
         * Removes an analyte from the list
         */
        public void remove(int i) {
            ProviderAnalyteViewDO data;

            data = analytes.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(ProviderAnalyteViewDO data) {
            analytes.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of analytes associated with this provider
         */
        public int count() {
            if (analytes != null)
                return analytes.size();
            return 0;
        }
    }
}