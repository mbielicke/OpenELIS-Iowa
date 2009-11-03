/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.cache.server;

import java.util.ArrayList;

import org.openelis.domain.DictionaryDO;
import org.openelis.server.handlers.DictionaryCacheHandler;

public class DictionaryCacheService {

    public DictionaryCacheRPC getIdBySystemName(String systemName) {
        DictionaryCacheRPC rpc = new DictionaryCacheRPC();
        DictionaryDO dictDO = DictionaryCacheHandler.getDictionaryDOFromSystemName(systemName);

        if (dictDO != null) {
            ArrayList list = new ArrayList();
            list.add(dictDO);
            rpc.dictionaryList = list;
            return rpc;

        } else
            return null;
    }

    public DictionaryCacheRPC getSystemNameById(Integer id) {
        DictionaryCacheRPC rpc = new DictionaryCacheRPC();
        DictionaryDO dictDO = DictionaryCacheHandler.getDictionaryDOFromId(id);

        if (dictDO != null) {
            ArrayList list = new ArrayList();
            list.add(dictDO);
            rpc.dictionaryList = list;
            return rpc;

        } else
            return null;
    }

    public DictionaryCacheRPC getListByCategorySystemName(String systemName) {
        DictionaryCacheRPC rpc = new DictionaryCacheRPC();
        rpc.dictionaryList = DictionaryCacheHandler.getListByCategorySystemName(systemName);
        return rpc;
    }
}
