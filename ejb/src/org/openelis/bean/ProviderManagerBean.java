package org.openelis.bean;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;
import org.openelis.remote.ProviderManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
@TransactionManagement(TransactionManagementType.BEAN)
public class ProviderManagerBean implements ProviderManagerRemote {
	
    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

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
		lockBean.getLock(ReferenceTable.PROVIDER, id);
		return fetchById(id);
	}

	public ProviderManager update(ProviderManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.PROVIDER, man.getProvider().getId());        
            man.update();
            lockBean.giveUpLock(ReferenceTable.PROVIDER, man.getProvider().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
	}
	
    public ProviderManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.PROVIDER,id);
        return fetchById(id);
    }

    public ProviderLocationManager fetchLocationByProviderId(Integer id) throws Exception {
        return ProviderLocationManager.fetchByProviderId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity("provider", flag);
    }
}
