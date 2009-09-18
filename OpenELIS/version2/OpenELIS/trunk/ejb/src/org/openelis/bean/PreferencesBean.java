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
package org.openelis.bean;

import org.openelis.domain.PreferencesDO;
import org.openelis.entity.Preferences;
import org.openelis.local.LoginLocal;
import org.openelis.remote.PreferencesRemote;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Stateless
public class PreferencesBean implements PreferencesRemote{
    
    @Resource
    SessionContext ctx;
    
    @EJB
    private LoginLocal login;
    
    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public PreferencesDO getPreferences(String key) {
        Query query = manager.createNamedQuery("getPreference");
        Integer sysUser = login.getSystemUserId();
        query.setParameter("systemUser", sysUser);
        query.setParameter("key", key);
        PreferencesDO prefDO = null;
        try {
            prefDO = (PreferencesDO)query.getSingleResult();
        }catch(NoResultException e){
            prefDO = new PreferencesDO();
            prefDO.setKey(key);
            prefDO.setSystemUserId(sysUser);
            prefDO.setText("<preferences></preferences>");
        }
        return prefDO;
    }

    public void storePreferences(PreferencesDO prefsDO) {
       Preferences prefs = null; 
       if(prefsDO.getId() != null){
           prefs = manager.find(Preferences.class,prefsDO.getId());
       }else{
           prefs = new Preferences();
       }
       prefs.setKey(prefsDO.getKey());
       prefs.setSystemUserId(login.getSystemUserId());
       prefs.setText(prefsDO.getText());
       if(prefs.getId() == null){
           manager.persist(prefs);
       }
    }
    

}
