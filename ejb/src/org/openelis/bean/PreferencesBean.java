/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.bean;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.PreferencesDO;
import org.openelis.entity.Preferences;
import org.openelis.remote.PreferencesRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;

@EJBs({
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class)
})

@Stateless
public class PreferencesBean implements PreferencesRemote{
    
    @Resource
    SessionContext ctx;
    
    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public PreferencesDO getPreferences(String key) {
        Query query = manager.createNamedQuery("getPreference");
        Integer sysUser = getSystemUserId();
        query.setParameter("systemUser", sysUser);
        query.setParameter("key", key);
        PreferencesDO prefDO = null;
        try {
            prefDO = (PreferencesDO)query.getSingleResult();
        }catch(NoResultException e){
            prefDO = new PreferencesDO();
            prefDO.setKey(key);
            prefDO.setSystem_user(sysUser);
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
       prefs.setSystemUserId(getSystemUserId());
       prefs.setText(prefsDO.getText());
       if(prefs.getId() == null){
           manager.persist(prefs);
       }
    }
    
    @PermitAll
    public Integer getSystemUserId() {
        try {
            InitialContext cont = new InitialContext();
            SystemUserUtilLocal sysUser = (SystemUserUtilLocal)cont.lookup("java:comp/env/ejb/SystemUser");
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
    }

}
