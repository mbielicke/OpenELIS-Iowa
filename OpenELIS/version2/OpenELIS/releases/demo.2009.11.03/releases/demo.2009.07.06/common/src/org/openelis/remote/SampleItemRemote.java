package org.openelis.remote;

import java.util.List;

import javax.ejb.Remote;

import org.openelis.manager.SampleItemsManager;

@Remote
public interface SampleItemRemote {
    
    public List getItemsBySampleId(Integer sampleId);
    
    public Integer update(SampleItemsManager sampleItems);

}
