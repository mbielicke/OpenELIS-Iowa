package org.openelis.modules.completeRelease.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleDataBundle;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("completeRelease")
public interface CompleteReleaseServiceInt extends RemoteService {

    public ArrayList<SampleDataBundle> query(Query query) throws Exception;
}