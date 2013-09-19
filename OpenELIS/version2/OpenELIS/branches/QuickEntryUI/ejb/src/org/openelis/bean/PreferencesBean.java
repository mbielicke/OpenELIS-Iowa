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
package org.openelis.bean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.entity.Preferences;

/**
 * Stateless bean used to persist and retrieve Preferences from the database
 * 
 */
@Stateless
@SecurityDomain("openelis")

public class PreferencesBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    /**
     * Returns the stored XML representation of the preferences for the passed
     * user
     */
    public String getPreferences(Integer user) {
        Preferences entity;

        entity = manager.find(Preferences.class, user);

        return entity != null ? entity.getText() : "<root></root>";

    }

    /**
     * Stores the passed preferences for the passed user into the database
     */
    public void setPreferences(Integer user, String prefs) {
        Preferences entity;
        boolean persist = false;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Preferences.class, user);
        if (entity == null) {
            entity = new Preferences();
            entity.setSystemUserId(user);
            persist = true;
        }
        entity.setText(prefs);

        if (persist)
            manager.persist(entity);
    }
}
