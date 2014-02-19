package org.openelis.modules.completeRelease1.client;

import java.util.ArrayList;

import org.openelis.ui.common.data.Query;
import org.openelis.manager.SampleDataBundle;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("completeRelease")
public interface CompleteReleaseServiceInt1 extends XsrfProtectedService {

    public ArrayList<SampleDataBundle> query(Query query) throws Exception;
}