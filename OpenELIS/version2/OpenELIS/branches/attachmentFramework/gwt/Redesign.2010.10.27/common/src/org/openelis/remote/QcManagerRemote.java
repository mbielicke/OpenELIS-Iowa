package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcManager;

@Remote
public interface QcManagerRemote {

    public QcManager fetchById(Integer id) throws Exception;

    public QcManager fetchWithAnalytes(Integer id) throws Exception;

    public QcManager add(QcManager man) throws Exception;

    public QcManager update(QcManager man) throws Exception;

    public QcManager fetchForUpdate(Integer id) throws Exception;

    public QcManager abortUpdate(Integer id) throws Exception;
    
    public QcAnalyteManager fetchAnalyteByQcId(Integer id) throws Exception;
}
