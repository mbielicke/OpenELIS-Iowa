package org.openelis.modules.testTrailer.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("testTrailer")
public interface TestTrailerServiceInt extends RemoteService {

    TestTrailerDO fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> fetchByName(String search) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    TestTrailerDO add(TestTrailerDO data) throws Exception;

    TestTrailerDO update(TestTrailerDO data) throws Exception;

    TestTrailerDO fetchForUpdate(Integer id) throws Exception;

    void delete(TestTrailerDO data) throws Exception;

    TestTrailerDO abortUpdate(Integer id) throws Exception;

}