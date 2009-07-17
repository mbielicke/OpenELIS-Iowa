package org.openelis.local;

import java.util.List;

import javax.ejb.Local;

import org.openelis.managerOld.SampleItemsManager;

@Local
public interface SampleItemLocal {

    public Integer update(SampleItemsManager sampleItems);
    
    public List getItemsBySampleId(Integer sampleId);
}
