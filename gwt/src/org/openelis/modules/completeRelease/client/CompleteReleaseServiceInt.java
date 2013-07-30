package org.openelis.modules.completeRelease.client;

import java.util.ArrayList;

import org.openelis.ui.common.data.Query;
import org.openelis.manager.SampleDataBundle;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("completeRelease")
public interface CompleteReleaseServiceInt extends XsrfProtectedService {

    public ArrayList<SampleDataBundle> query(Query query) throws Exception;
}