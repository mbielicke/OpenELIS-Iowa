package org.openelis.bean;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class ProviderManagerBean {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockBean      lockBean;
    
    @EJB
    private UserCacheBean  userCache;

    public ProviderManager fetchById(Integer id) throws Exception {
        return ProviderManager.fetchById(id);
    }

    public ProviderManager fetchWithLocations(Integer id) throws Exception {
        return ProviderManager.fetchWithLocations(id);
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        return ProviderManager.fetchWithNotes(id);
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            man.add();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;

    }

    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        ProviderManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();

            lockBean.lock(Constants.table().PROVIDER, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(Constants.table().PROVIDER, man.getProvider().getId());
            man.update();
            lockBean.unlock(Constants.table().PROVIDER, man.getProvider().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(Constants.table().PROVIDER, id);
        return fetchById(id);
    }

    public ProviderLocationManager fetchLocationByProviderId(Integer id) throws Exception {
        return ProviderLocationManager.fetchByProviderId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("provider", flag);
    }
}
