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

import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ProviderLocationDO;

/**
 * This class is used to bulk load order manager.
 */
public class ProviderManager1Accessor {
    /**
     * Set/get objects from order manager
     */
    public static ProviderDO getProvider(ProviderManager1 pm) {
        return pm.provider;
    }

    public static void setProvider(ProviderManager1 pm, ProviderDO provider) {
        pm.provider = provider;
    }

    public static ArrayList<ProviderLocationDO> getLocations(ProviderManager1 pm) {
        return pm.locations;
    }

    public static void setLocations(ProviderManager1 pm, ArrayList<ProviderLocationDO> locations) {
        pm.locations = locations;
    }

    public static void addLocation(ProviderManager1 pm, ProviderLocationDO location) {
        if (pm.locations == null)
            pm.locations = new ArrayList<ProviderLocationDO>();
        pm.locations.add(location);
    }

    public static ArrayList<NoteViewDO> getNotes(ProviderManager1 pm) {
        return pm.notes;
    }

    public static void setNotes(ProviderManager1 pm, ArrayList<NoteViewDO> notes) {
        pm.notes = notes;
    }

    public static void addNote(ProviderManager1 pm, NoteViewDO note) {
        if (pm.notes == null)
            pm.notes = new ArrayList<NoteViewDO>();
        pm.notes.add(note);
    }

    public static ArrayList<DataObject> getRemoved(ProviderManager1 pm) {
        return pm.removed;
    }

    public static void setRemoved(ProviderManager1 pm, ArrayList<DataObject> removed) {
        pm.removed = removed;
    }

}