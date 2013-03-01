package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.ProjectParameterDO;
import org.openelis.entity.ProjectParameter;
import org.openelis.meta.ProjectMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class ProjectParameterBean {

    @PersistenceContext(unitName = "openelis")
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
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), 
                                             ProjectMeta.getProjectParameterParameter()));
        if (DataBaseUtil.isEmpty(data.getOperationId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ProjectMeta.getProjectParameterOperationId()));
        if (DataBaseUtil.isEmpty(data.getValue()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ProjectMeta.getProjectParameterValue()));
        if (list.size() > 0)
            throw list;
    }
}
