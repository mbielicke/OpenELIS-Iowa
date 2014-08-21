package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;

@Remote
public interface ProjectManagerRemote {

    public ProjectManager fetchById(Integer id) throws Exception;

    public ProjectManager fetchWithParameters(Integer id) throws Exception;

    public ProjectManager add(ProjectManager man) throws Exception;

    public ProjectManager update(ProjectManager man) throws Exception;

    public ProjectManager fetchForUpdate(Integer id) throws Exception;

    public ProjectManager abortUpdate(Integer id) throws Exception;

    public ProjectParameterManager fetchParameterByProjectId(Integer id) throws Exception;
}
