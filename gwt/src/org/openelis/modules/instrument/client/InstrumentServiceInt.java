package org.openelis.modules.instrument.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InstrumentLogManager;
import org.openelis.manager.InstrumentManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("instrument")
public interface InstrumentServiceInt extends RemoteService {

    InstrumentManager fetchById(Integer id) throws Exception;

    ArrayList<InstrumentViewDO> fetchByName(String name) throws Exception;

    ArrayList<InstrumentViewDO> fetchActiveByName(String name) throws Exception;

    InstrumentManager fetchWithLogs(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    InstrumentManager add(InstrumentManager man) throws Exception;

    InstrumentManager update(InstrumentManager man) throws Exception;

    InstrumentManager fetchForUpdate(Integer id) throws Exception;

    InstrumentManager abortUpdate(Integer id) throws Exception;

    InstrumentLogManager fetchLogByInstrumentId(Integer id) throws Exception;

}