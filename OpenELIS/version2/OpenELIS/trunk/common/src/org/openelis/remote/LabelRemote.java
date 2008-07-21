/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.LabelDO;

@Remote
public interface LabelRemote {
    // method to return Label 
    public LabelDO getLabel(Integer labelId);
    
    public LabelDO getLabelAndUnlock(Integer labelId, String session);
    
    public LabelDO getLabelAndLock(Integer labelId, String session)throws Exception;    
     
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
