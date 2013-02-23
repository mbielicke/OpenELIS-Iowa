package org.openelis.modules.section.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SectionManager;
import org.openelis.manager.SectionParameterManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("section")
public interface SectionServiceInt extends RemoteService {

    SectionManager fetchById(Integer id) throws Exception;

    ArrayList<SectionDO> fetchByName(String search) throws Exception;

    SectionManager fetchWithParameters(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    SectionManager add(SectionManager man) throws Exception;

    SectionManager update(SectionManager man) throws Exception;

    SectionManager fetchForUpdate(Integer id) throws Exception;

    SectionManager abortUpdate(Integer id) throws Exception;

    //
    // support for SectionParameterManager
    //   
    SectionParameterManager fetchParameterBySectionId(Integer id) throws Exception;

}