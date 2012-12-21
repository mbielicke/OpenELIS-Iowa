package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sampleTracking")
public interface SampleTrackingServiceInt extends RemoteService {

    ArrayList<SampleManager> query(Query query) throws Exception;

}