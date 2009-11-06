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

import javax.naming.InitialContext;

import org.openelis.domain.CategoryDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.CategoryLocal;
import org.openelis.utilcommon.DataBaseUtil;


public class CategoryManagerProxy {
    
    public CategoryManager fetchById(Integer id)throws Exception {
        CategoryDO data;
        CategoryManager m;
        
        data = local().fetchById(id);
        m = CategoryManager.getInstance();
        
        m.setCategory(data);
        
        return m;
    }
    
    public CategoryManager fetchWithEntries(Integer id)throws Exception {
        CategoryManager m;
        
        m = fetchById(id);
        m.getEntries();
        
        return m;
    }
    
    public CategoryManager add(CategoryManager man) throws Exception {
        Integer id;

        local().add(man.getCategory());
        id = man.getCategory().getId();

        man.getEntries().setCategoryId(id);
        man.getEntries().add();

        return man;
    }
    
    public CategoryManager update(CategoryManager man) throws Exception {
        Integer id;

        local().update(man.getCategory());
        id = man.getCategory().getId();

        man.getEntries().setCategoryId(id);
        man.getEntries().update();

        return man;
    }    
    
    public CategoryManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public CategoryManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(CategoryManager man) throws Exception {        
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            local().validate(man.getCategory());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            man.getEntries().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }
    
    private CategoryLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (CategoryLocal)ctx.lookup("openelis/CategoryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
