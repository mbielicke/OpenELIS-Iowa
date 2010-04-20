/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisUserLocal;

public class AnalysisUserManagerProxy {
    public AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception {
        AnalysisUserViewDO anDO;
        ArrayList<AnalysisUserViewDO> items;
        AnalysisUserManager man;
        
        items = local().fetchByAnalysisId(analysisId);
        man = AnalysisUserManager.getInstance();
        
        for(int i=0; i<items.size(); i++){
            anDO = items.get(i);
            man.addAnalysisUser(anDO);
        }
        
        man.setAnalysisId(analysisId);
        
        return man;
    }

    public AnalysisUserManager add(AnalysisUserManager man) throws Exception {
        AnalysisUserViewDO anUser;
        AnalysisUserLocal l;
        
        l = local();
        for (int i = 0; i < man.count(); i++ ) {
            anUser = man.getAnalysisUserAt(i);
            anUser.setAnalysisId(man.getAnalysisId());
            
            l.add(anUser);
        }

        return man;
    }

   public AnalysisUserManager update(AnalysisUserManager man) throws Exception {
       AnalysisUserViewDO anUser;
       AnalysisUserLocal l;
       
       l = local();
       for(int j=0; j<man.deleteCount(); j++){
           l.delete(man.getDeletedAt(j));
       }
       
       for(int i=0; i<man.count(); i++){
           anUser = man.getAnalysisUserAt(i);
           
           if(anUser.getId() == null){
               anUser.setAnalysisId(man.getAnalysisId());
               l.add(anUser);
           }else
               l.update(anUser);
       }

       return man;
   }

   public void validate(AnalysisUserManager man, ValidationErrorsList errorsList) throws Exception {
       
   }
   
   private AnalysisUserLocal local(){
       try{
           InitialContext ctx = new InitialContext();
           return (AnalysisUserLocal)ctx.lookup("openelis/AnalysisUserBean/local");
       }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
       }
   }
}
