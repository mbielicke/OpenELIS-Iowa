package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.SampleProjectDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.TableDataRow;

public class SampleProjectManager implements RPC {
    private static final long                            serialVersionUID = 1L;

    protected Integer                                    sampleId;

    protected ArrayList<SampleProjectDO>                 projects;
    protected ArrayList<SampleProjectDO>                 deletedList;

    protected transient static SampleProjectManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleProjectManager getInstance() {
        SampleProjectManager spm;

        spm = new SampleProjectManager();
        spm.projects = new ArrayList<SampleProjectDO>();

        return spm;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static SampleProjectManager findBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    public SampleProjectDO getFirstPermanentProject(){
        int i;
        SampleProjectDO project;
        
        for(i=0;i<projects.size(); i++){
            project = projects.get(i);
            if("Y".equals(project.getIsPermanent()))
                return project;
        }
        return null;
    }
    
    public void addFirstPermanentProject(SampleProjectDO newProject) {
        SampleProjectDO oldProject = getFirstPermanentProject();

        if (oldProject == null && newProject != null) { // insert
            addProject(newProject);
        } else if (oldProject != null && newProject == null) { // delete
            removeProjectAt(0);
        } else if (oldProject != null && newProject != null) { // update
            setProjectAt(newProject, 0);
        }
    }

    public int count() {
        if (projects == null)
            return 0;

        return projects.size();
    }

    public SampleProjectDO getProjectAt(int i) {
        return projects.get(i);
    }

    public void setProjectAt(SampleProjectDO project, int i) {
        projects.set(i, project);
    }

    public void addProject(SampleProjectDO project) {
        if (projects == null)
            projects = new ArrayList<SampleProjectDO>();

        projects.add(project);
    }

    public void addProjectAt(SampleProjectDO project, int i) {
        if (projects == null)
            projects = new ArrayList<SampleProjectDO>();

        if("Y".equals(project.getIsPermanent()))
            projects.add(0, project);
        else
            projects.add(i, project);
    }

    public void removeProjectAt(int i) {
        if (projects == null || i >= projects.size())
            return;

        SampleProjectDO tmpDO = projects.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<SampleProjectDO>();

        deletedList.add(tmpDO);
    }
    // service methods
    public SampleProjectManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleProjectManager update() throws Exception {
        return proxy().update(this);
    }

    private static SampleProjectManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleProjectManagerProxy();

        return proxy;
    }

    // these are friendly methods so only managers and proxies can call this method
    ArrayList<SampleProjectDO> getProjects() {
        return projects;
    }

    void setProjects(ArrayList<SampleProjectDO> projects) {
        this.projects = projects;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    SampleProjectDO getDeletedAt(int i) {
        return deletedList.get(i);
    }
}