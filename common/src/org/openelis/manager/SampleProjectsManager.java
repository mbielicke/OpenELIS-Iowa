package org.openelis.manager;

import java.util.ArrayList;
import java.util.List;

import org.openelis.domain.SampleProjectDO;
import org.openelis.gwt.common.RPC;

public class SampleProjectsManager implements RPC {
    private static final long                      serialVersionUID = 1L;

    Integer                                        sampleId;

    protected ArrayList<SampleProjectDO>           items;
    protected boolean                              cached;
    protected transient SampleProjectsManagerIOInt manager;

    /**
     * Creates a new instance of this object.
     */
    public static SampleProjectsManager getInstance() {
        SampleProjectsManager spm;

        spm = new SampleProjectsManager();
        spm.items = new ArrayList<SampleProjectDO>();

        return spm;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static SampleProjectsManager findBySampleId(Integer sampleId) {
        SampleProjectsManager spm = new SampleProjectsManager();
        spm.items = new ArrayList<SampleProjectDO>();
        
        spm.setSampleId(sampleId);
        spm.fetch();

        return spm;
    }

    public int count() {
        fetch();
        return items.size();
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public SampleProjectDO add() {
        SampleProjectDO newProject = new SampleProjectDO();
        items.add(newProject);

        return newProject;
    }

    public SampleProjectDO getSampleProjectAt(int i) {
        fetch();
        return items.get(i);

    }

    public void setSampleProjectAt(SampleProjectDO sampleProject, int i) {
        items.add(i, sampleProject);
    }

    public SampleProjectsManagerIOInt getManager() {
        return manager;
    }

    public void setManager(SampleProjectsManagerIOInt manager) {
        this.manager = manager;
    }

    public void validate() {

    }

    // manager methods
    public void update() {
        manager().update(this);
    }

    protected void fetch() {
        if (cached)
            return;

        cached = true;
        List sampleProjects = manager().fetch(sampleId);

        for (int i = 0; i < sampleProjects.size(); i++)
            items.add((SampleProjectDO)sampleProjects.get(i));
    }

    private SampleProjectsManagerIOInt manager() {
        if (manager == null)
            manager = ManagerFactory.getSampleProjectManagerIO();

        return manager;
    }
}
