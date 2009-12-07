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
import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;
import org.openelis.remote.ProjectManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("project-select")
@TransactionManagement(TransactionManagementType.BEAN)
public class ProjectManagerBean implements ProjectManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

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
        ut.begin();
        man.add();
        ut.commit();

        return man;
    }

    public ProjectManager update(ProjectManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        lockBean.validateLock(ReferenceTable.PROJECT, man.getProject().getId());
        man.update();
        lockBean.giveUpLock(ReferenceTable.PROJECT, man.getProject().getId());
        ut.commit();

        return man;
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.PROJECT, id);
        return fetchById(id);
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.PROJECT, id);
        return fetchById(id);
    }

    public ProjectParameterManager fetchParameterByProjectId(Integer id) throws Exception {
        return ProjectParameterManager.fetchByProjectId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "project", flag);
    }
}
