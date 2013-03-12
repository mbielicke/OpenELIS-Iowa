package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SampleTrackingServiceIntAsync {

    void query(Query query, AsyncCallback<ArrayList<SampleManager>> callback);

}
