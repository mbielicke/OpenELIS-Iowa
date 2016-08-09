package org.openelis.modules.eventLog.client;

import java.util.ArrayList;

import org.openelis.domain.EventLogDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("eventLog")
public interface EventLogServiceInt extends XsrfProtectedService {

    ArrayList<EventLogDO> query(Query query) throws Exception;
    
    EventLogDO add(Integer typeId, String source, Integer referenceTableId,
                   Integer referenceId, Integer levelId, String text) throws Exception;

}