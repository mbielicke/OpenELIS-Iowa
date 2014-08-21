package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.SectionViewDO;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("sectionCache")
public interface SectionCacheServiceInt extends XsrfProtectedService {

    SectionViewDO getById(Integer id) throws Exception;

    ArrayList<SectionViewDO> getList(String name) throws Exception;

}