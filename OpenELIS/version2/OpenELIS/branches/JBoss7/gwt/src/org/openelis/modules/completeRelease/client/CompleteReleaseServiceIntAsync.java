package org.openelis.modules.completeRelease.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleDataBundle;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CompleteReleaseServiceIntAsync {

    void query(Query query, AsyncCallback<ArrayList<SampleDataBundle>> callback);

}
