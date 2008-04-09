package org.openelis.bean;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

import org.openelis.domain.PreferencesDO;
import org.openelis.entity.Preferences;
import org.openelis.remote.PreferencesRemote;

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
       prefs.setSystemUser(getSystemUserId());
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
