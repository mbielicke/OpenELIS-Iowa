package org.openelis.remote;

import java.util.ArrayList;

import org.openelis.domain.SectionViewDO;

public interface SectionCacheRemote {
    public abstract SectionViewDO getById(Integer id) throws Exception;

    public abstract ArrayList<SectionViewDO> getList() throws Exception;
}