package org.openelis.local;

import java.util.ArrayList;

import org.openelis.domain.SectionViewDO;

public interface SectionCacheLocal {

    public abstract SectionViewDO getById(Integer id) throws Exception;

    public abstract ArrayList<SectionViewDO> getList() throws Exception;

    public abstract void evict(Integer id);

}