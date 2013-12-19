package org.openelis.modules.completeRelease1.client;

import java.util.ArrayList;

import org.openelis.ui.common.data.Query;
import org.openelis.manager.SampleDataBundle;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CompleteReleaseServiceInt1Async {

    void query(Query query, AsyncCallback<ArrayList<SampleDataBundle>> callback);

}
