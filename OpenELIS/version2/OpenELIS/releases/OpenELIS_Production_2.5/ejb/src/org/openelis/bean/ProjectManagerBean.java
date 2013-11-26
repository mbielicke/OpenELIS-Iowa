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
import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;
import org.openelis.ui.common.ModulePermission.ModuleFlags;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class ProjectManagerBean {
    @Resource
    private SessionContext ctx;

    @EJB
    private LockBean      lockBean;
    
    @EJB
    private UserCacheBean  userCache;

    public ProjectManagerBean() {
    }

    public ProjectManager fetchById(Integer id) throws Exception {
        return ProjectManager.fetchById(id);
    }

    public ProjectManager fetchWithParameters(Integer id) throws Exception {
        return ProjectManager.fetchWithParameters(id);
    }

    public ProjectManager add(ProjectManager man) throws Exception {
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

    public ProjectManager update(ProjectManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(Constants.table().PROJECT, man.getProject().getId());
            man.update();
            lockBean.unlock(Constants.table().PROJECT, man.getProject().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        ProjectManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.lock(Constants.table().PROJECT, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(Constants.table().PROJECT, id);
        return fetchById(id);
    }

    public ProjectParameterManager fetchParameterByProjectId(Integer id) throws Exception {
        return ProjectParameterManager.fetchByProjectId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("project", flag);
    }
}
