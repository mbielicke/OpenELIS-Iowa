package org.openelis.manager;

import java.util.List;

import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleItemRemote;

public class SampleItemsManagerIOClient implements SampleItemsManagerIOInt {

    public List fetch(Integer sampleId) {
        SampleItemRemote remote = getSampleItemRemote();

        return remote.getItemsBySampleId(sampleId);
    }

    public Integer update(SampleItemsManager sampleItems) {
        SampleItemRemote remote = getSampleItemRemote();

        return remote.update(sampleItems);
    }
    
    private SampleItemRemote getSampleItemRemote(){
        return (SampleItemRemote)EJBFactory.lookup("openelis/SampleItemBean/remote");
    }
}
