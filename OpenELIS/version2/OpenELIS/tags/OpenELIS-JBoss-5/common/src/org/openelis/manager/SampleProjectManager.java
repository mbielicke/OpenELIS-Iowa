package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleProjectManager implements RPC {
    private static final long                            serialVersionUID = 1L;

    protected Integer                                    sampleId;
    protected ArrayList<SampleProjectViewDO>             projects, deletedList;

    protected transient static SampleProjectManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleProjectManager getInstance() {
        SampleProjectManager spm;

        spm = new SampleProjectManager();
        spm.projects = new ArrayList<SampleProjectViewDO>();

        return spm;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use
     * this function to load an instance of this object from database.
     */
    public static SampleProjectManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    // service methods
    public SampleProjectManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleProjectManager update() throws Exception {
        return proxy().update(this);
    }

    public SampleProjectViewDO getProjectAt(int i) {
        return projects.get(i);
    }

    public void setProjectAt(SampleProjectViewDO project, int i) {
        projects.set(i, project);
    }

    public void addProject(SampleProjectViewDO project) {
        if (projects == null)
            projects = new ArrayList<SampleProjectViewDO>();

        projects.add(project);
    }

    public void addProjectAt(SampleProjectViewDO project, int i) {
        if (projects == null)
            projects = new ArrayList<SampleProjectViewDO>();

        if ("Y".equals(project.getIsPermanent()))
            projects.add(0, project);
        else
            projects.add(i, project);
    }
    
    public void removeProject(SampleProjectViewDO data) {
        if (projects == null)
            return;

        projects.remove(data);

        if (data.getId() != null) {
            if (deletedList == null)
                deletedList = new ArrayList<SampleProjectViewDO>();
            deletedList.add(data);
        }
    }

    public void removeProjectAt(int i) {
        SampleProjectViewDO data;
        if (projects == null || i >= projects.size())
            return;

        data = projects.remove(i);        

        if (data.getId() != null) {
            if (deletedList == null)
                deletedList = new ArrayList<SampleProjectViewDO>();
            deletedList.add(data);
        }
    }   

    //
    //helper methods
    //
    public SampleProjectViewDO getFirstPermanentProject() {
        int i;
        SampleProjectViewDO project;

        for (i = 0; i < projects.size(); i++ ) {
            project = projects.get(i);
            if ("Y".equals(project.getIsPermanent()))
                return project;
        }
        return null;
    }

    public void addFirstPermanentProject(SampleProjectViewDO newProject) {
        SampleProjectViewDO oldProject = getFirstPermanentProject();

        if (oldProject == null && newProject != null) { // insert
            addProjectAt(newProject, 0);
        } else if (oldProject != null && newProject == null) { // delete
            removeProjectAt(0);
        } else if (oldProject != null && newProject != null) { // update
            setProjectAt(newProject, 0);
        }
    }   
    
    public void removeFirstPermanentProject() {
        SampleProjectViewDO data;
        
        data = getFirstPermanentProject();
        if (data != null)
            removeProject(data);                
    }
   

    public int count() {
        if (projects == null)
            return 0;

        return projects.size();
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getSampleId() {
        return sampleId;
    }

    void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    ArrayList<SampleProjectViewDO> getProjects() {
        return projects;
    }

    void setProjects(ArrayList<SampleProjectViewDO> projects) {
        this.projects = projects;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    SampleProjectViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }

    private static SampleProjectManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleProjectManagerProxy();

        return proxy;
    }
}