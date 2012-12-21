package org.openelis.modules.eventLog.client;

import java.util.ArrayList;

import org.openelis.domain.EventLogDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("eventLog")
public interface EventLogServiceInt extends RemoteService {

    ArrayList<EventLogDO> query(Query query) throws Exception;

}