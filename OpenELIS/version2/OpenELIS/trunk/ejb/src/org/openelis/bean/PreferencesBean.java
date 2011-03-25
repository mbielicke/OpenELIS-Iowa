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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.PreferencesDO;
import org.openelis.entity.Preferences;
import org.openelis.remote.PreferencesRemote;
import org.openelis.utils.EJBFactory;

@Stateless
public class PreferencesBean implements PreferencesRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    public PreferencesDO getPreferences(String key) {
        Query query;
        Integer userId;
        PreferencesDO data;

        try {
            userId = EJBFactory.getUserCache().getId();
        } catch (Exception e) {
            e.printStackTrace();
            userId = null;
        }

        query = manager.createNamedQuery("getPreference");
        query.setParameter("systemUser", userId);
        query.setParameter("key", key);

        try {
            data = (PreferencesDO)query.getSingleResult();
        } catch (NoResultException e) {
            data = new PreferencesDO();
            data.setKey(key);
            data.setSystemUserId(userId);
            data.setText("<preferences></preferences>");
        }
        return data;
    }

    public void setPreferences(PreferencesDO data) {
        Preferences entity;

        if ( !data.isChanged())
            return;

        if (data.getId() != null) {
            entity = manager.find(Preferences.class, data.getId());
        } else {
            entity = new Preferences();
            try {
                entity.setSystemUserId(EJBFactory.getUserCache().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        entity.setKey(data.getKey());
        entity.setText(data.getText());

        if (entity.getId() == null)
            manager.persist(entity);
    }
}
