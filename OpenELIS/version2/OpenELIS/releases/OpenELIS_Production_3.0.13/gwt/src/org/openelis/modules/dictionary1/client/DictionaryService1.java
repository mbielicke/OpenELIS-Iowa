package org.openelis.modules.dictionary1.client;

import java.util.ArrayList;

import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.manager.CategoryManager1;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("dictionary1")
public interface DictionaryService1 extends XsrfProtectedService {

    CategoryManager1 fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> fetchByEntry(Query query) throws Exception;

    ArrayList<IdNameVO> fetchByCategoryName(String name) throws Exception;

    ArrayList<DictionaryViewDO> fetchByEntry(String entry) throws Exception;

    ArrayList<DictionaryViewDO> fetchByExactEntry(String entry) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    CategoryManager1 add(CategoryManager1 man) throws Exception;

    CategoryManager1 update(CategoryManager1 man) throws Exception;

    CategoryManager1 fetchForUpdate(Integer id) throws Exception;

    CategoryManager1 abortUpdate(Integer id) throws Exception;

    DictionaryViewDO validateForDelete(DictionaryViewDO data) throws Exception;

    CategoryManager1 sort(CategoryManager1 cm, boolean ascending) throws Exception;
}