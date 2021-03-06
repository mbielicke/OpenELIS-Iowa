package org.openelis.modules.eventLog.client;

import java.util.ArrayList;

import org.openelis.domain.EventLogDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventLogServiceIntAsync {

    void query(Query query, AsyncCallback<ArrayList<EventLogDO>> callback);

    void add(Integer typeId, String source, Integer referenceTableId, Integer referenceId,
             Integer levelId, String text, AsyncCallback<EventLogDO> callback);

}
