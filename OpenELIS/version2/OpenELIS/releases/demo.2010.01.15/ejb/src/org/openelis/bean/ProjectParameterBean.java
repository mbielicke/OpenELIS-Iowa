package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ProjectParameterDO;
import org.openelis.entity.ProjectParameter;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ProjectParameterLocal;
import org.openelis.meta.ProjectMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("project-select")
public class ProjectParameterBean implements ProjectParameterLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager         manager;

    public ArrayList<ProjectParameterDO> fetchByProjectId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("ProjectParameter.FetchByProjectId");
        query.setParameter("projectId", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ProjectParameterDO add(ProjectParameterDO data) throws Exception {
        ProjectParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new ProjectParameter();
        entity.setProjectId(data.getProjectId());
        entity.setParameter(data.getParameter());
        entity.setOperationId(data.getOperationId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ProjectParameterDO update(ProjectParameterDO data) throws Exception {
        ProjectParameter entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(ProjectParameter.class, data.getId());
        entity.setProjectId(data.getProjectId());
        entity.setParameter(data.getParameter());
        entity.setOperationId(data.getOperationId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(ProjectParameterDO data) throws Exception {
        ProjectParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(ProjectParameter.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(ProjectParameterDO data) throws ValidationErrorsList {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getParameter()))
            list.add(new FieldErrorException("fieldRequiredException", 
                                             ProjectMeta.getProjectParameterParameter()));
        if (DataBaseUtil.isEmpty(data.getOperationId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             ProjectMeta.getProjectParameterOperationId()));
        if (DataBaseUtil.isEmpty(data.getValue()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             ProjectMeta.getProjectParameterValue()));
        if (list.size() > 0)
            throw list;
    }
}
