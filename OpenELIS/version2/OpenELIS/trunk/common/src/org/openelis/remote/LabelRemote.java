package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.LabelDO;

@Remote
public interface LabelRemote {
    // method to return Label 
    public LabelDO getLabel(Integer labelId);
    
    public LabelDO getLabelAndUnlock(Integer labelId);
    
    public LabelDO getLabelAndLock(Integer labelId)throws Exception;    
     
    //  commit a change to Label or insert a new Label
    public Integer updateLabel(LabelDO sysVarDO)throws Exception;
    
    //  method to query for Label
    public List query(HashMap fields, int first, int max) throws Exception;
    
    //a way for the servlet to get the system user id
    public Integer getSystemUserId();
    
    public List<Object[]> getScriptlets();
    
    public List<Exception> validateForAdd(LabelDO labelDO);
    
    public List<Exception> validateForUpdate(LabelDO labelDO);
    
    public List<Exception> validateForDelete(Integer labelId);
    
    public void deleteLabel(Integer labelId) throws Exception;
}
