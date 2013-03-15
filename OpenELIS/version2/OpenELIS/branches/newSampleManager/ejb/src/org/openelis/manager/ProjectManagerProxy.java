package org.openelis.manager;

import org.openelis.domain.ProjectViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class ProjectManagerProxy {

    public ProjectManager fetchById(Integer id) throws Exception {
        ProjectViewDO data;
        ProjectManager m;

        data = EJBFactory.getProject().fetchById(id);
        m = ProjectManager.getInstance();
        m.setProject(data);

        return m;
    }

    public ProjectManager fetchWithParameters(Integer id) throws Exception {
        ProjectViewDO data;
        ProjectManager m;

        data = EJBFactory.getProject().fetchById(id);
        m = ProjectManager.getInstance();

        m.setProject(data);
        m.getParameters();

        return m;
    }

    public ProjectManager add(ProjectManager man) throws Exception {
        Integer id;

        EJBFactory.getProject().add(man.getProject());
        id = man.getProject().getId();

        if (man.parameters != null) {
            man.getParameters().setProjectId(id);
            man.getParameters().add();
        }
        return man;
    }

    public ProjectManager update(ProjectManager man) throws Exception {
        Integer id;

        EJBFactory.getProject().update(man.getProject());
        id = man.getProject().getId();

        if (man.parameters != null) {
            man.getParameters().setProjectId(id);
            man.getParameters().update();
        }
        return man;
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(ProjectManager man) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        try {
            EJBFactory.getProject().validate(man.getProject());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.parameters != null)
                man.getParameters().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        if (list.size() > 0)
            throw list;
    }
}