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
package org.openelis.modules.favorites.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.Preferences;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.modules.favorites.client.FavoritesServiceInt;
import org.openelis.server.PreferencesManager;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FavoritesService implements FavoritesServiceInt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    private static HashMap items = new HashMap();
    
    {items.put("ProviderScreen", "provider");
     items.put("OrganizationScreen","organization");
     items.put("QAEventScreen","QAEvent");
     items.put("AnalyteScreen", "analyte");
     items.put("DictionaryScreen", "dictionary");
     items.put("StandardNoteScreen", "standardNote");
     items.put("StorageUnitScreen", "storageUnit");
     items.put("StorageLocationScreen", "storageLocation");
    }
                                               
    
	public String getFavorites(Preferences prefs) {
        try {
            if(prefs == null)
                prefs = PreferencesManager.getUser(this.getClass());
            Document doc = XMLUtil.createNew("doc");
            Element root = doc.getDocumentElement();
            if(prefs.get("favorites") != null){
                String[] favorites = prefs.get("favorites").split(",");
                for(int i = 0; i < favorites.length; i++){ 
                    Element favorite = doc.createElement("favorite");
                    favorite.setAttribute("label",(String)items.get(favorites[i]));
                    favorite.setAttribute("value", favorites[i]);
                    root.appendChild(favorite);
                }
            }
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/favoritesMenu.xsl",doc);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
	}


    public String getEditFavorites() {
        try {
            Document doc = XMLUtil.createNew("doc");
            Element root = doc.getDocumentElement();
            Iterator itemsIt = items.keySet().iterator();
            Preferences prefs = PreferencesManager.getUser(this.getClass());
            List favList = new ArrayList();
            if(prefs.get("favorites") != null){
                String[] favs = prefs.get("favorites").split(",");
                favList = Arrays.asList(favs);
            }
            while(itemsIt.hasNext()){
                String key = (String)itemsIt.next();
                String label = (String)items.get(key);
                Element favorite = doc.createElement("favorite");
                favorite.setAttribute("label",label);
                favorite.setAttribute("value",key);
                if(favList.contains(key))
                    favorite.setAttribute("selected","Y");
                else
                    favorite.setAttribute("selected","N");
                root.appendChild(favorite);
            }
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/favoritesEdit.xsl",doc);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public String saveFavorites(FormRPC rpc) {
        // TODO Auto-generated method stub
        Iterator rpcIt = rpc.getFieldMap().keySet().iterator();
        StringBuffer favorites = new StringBuffer();
        while(rpcIt.hasNext()){
            String key = (String)rpcIt.next();
            if("Y".equals(rpc.getFieldValue(key))){
                if(favorites.length() != 0){
                    favorites.append(",");
                }
                favorites.append(key);
            }
        }
        Preferences prefs = PreferencesManager.getUser(this.getClass());
        if(!favorites.toString().equals(""))
            prefs.put("favorites", favorites.toString());
        else
            prefs.removePreference("favorites");
        PreferencesManager.store(prefs);
        return getFavorites(prefs);
    }
}
