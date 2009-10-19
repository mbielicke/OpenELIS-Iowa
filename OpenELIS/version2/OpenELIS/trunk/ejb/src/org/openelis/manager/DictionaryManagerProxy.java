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

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.local.CategoryLocal;
import org.openelis.local.JMSMessageProducerLocal;
import org.openelis.messages.DictionaryCacheMessage;

public class DictionaryManagerProxy {
    
    public DictionaryManager fetchByCategoryId(Integer categoryId)throws Exception {
        CategoryDO catDO;
        DictionaryManager man;
        ArrayList<DictionaryViewDO> entries;
        
        catDO = local().fetchByCategoryId(categoryId); 
        man = DictionaryManager.getInstance();
        man.setCategory(catDO);
        
        entries = (ArrayList<DictionaryViewDO>)local().getEntries(categoryId);
        man.entries = entries;
        
        return man;
    }

    public DictionaryManager add(DictionaryManager man) throws Exception {
        DictionaryViewDO entry;      

        local().add(man.getCategory());
        
        man.setCategoryId(man.getCategory().getId());
        
        for(int i = 0; i < man.count(); i++) {
            entry = man.getEntryAt(i);
            entry.setSortOrder(i+1);
            entry.setCategoryId(man.getCategoryId());
             
            local().addDictionary(entry);
        }
                       
        return man;
    }
    
    public DictionaryManager update(DictionaryManager man) throws Exception {
        DictionaryViewDO entry;
        int i;
        DictionaryCacheMessage msg;
        CategoryDO catDO;
        boolean sendMessage;
        
        catDO = man.getCategory();
        sendMessage = false;
        
        local().update(catDO);       
        man.setCategoryId(catDO.getId());
        
        for(i = 0; i < man.deleteCount(); i++){
            local().deleteDictionary(man.getDeletedAt(i));
            sendMessage = true;
        }
        
        for(i = 0; i < man.count(); i++) {
            entry = man.getEntryAt(i);
            entry.setSortOrder(i+1);
            
            if(entry.getId() == null) {
                entry.setCategoryId(man.getCategoryId());
                local().addDictionary(entry);
                sendMessage = true;
            } else {               
                local().updateDictionary(entry);
                if(entry.isChanged())
                    sendMessage = true;
            }
        }     
        
        if(sendMessage) {            
            //invalidate the cache
            msg = new DictionaryCacheMessage();
            msg.setCatDO(catDO);
            msg.action = DictionaryCacheMessage.Action.UPDATED;
            jmsLocal().writeMessage(msg);
        }
        
        return man;
    }   
    
    public DictionaryManager fetchForUpdate(Integer testId) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public DictionaryManager abort(Integer testId) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(DictionaryManager man) throws Exception {
        local().validate(man.getCategory(), man.getEntries());
    }
    
    private CategoryLocal local() {
        try{
            InitialContext ctx = new InitialContext();
            return (CategoryLocal)ctx.lookup("openelis/CategoryBean/local");
        } catch(Exception e){
            System.out.println(e.getMessage());
            return null;
       }
    }
    
    private JMSMessageProducerLocal jmsLocal() {
        try{
            InitialContext ctx = new InitialContext();
            return (JMSMessageProducerLocal)ctx.lookup("openelis/JMSMessageProducerBean/local");
        } catch(Exception e){
            System.out.println(e.getMessage());
            return null;
       }
    }
}
