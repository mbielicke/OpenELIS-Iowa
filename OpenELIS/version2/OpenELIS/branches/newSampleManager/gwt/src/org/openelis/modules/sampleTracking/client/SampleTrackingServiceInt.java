package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;

import org.openelis.manager.SampleManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("sampleTracking")
public interface SampleTrackingServiceInt extends XsrfProtectedService {

    ArrayList<SampleManager> query(Query query) throws Exception;

}