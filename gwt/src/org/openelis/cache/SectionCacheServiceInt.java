package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.SectionViewDO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sectionCache")
public interface SectionCacheServiceInt extends RemoteService {

    SectionViewDO getById(Integer id) throws Exception;

    ArrayList<SectionViewDO> getList(String name) throws Exception;

}