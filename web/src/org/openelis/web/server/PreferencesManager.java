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
package org.openelis.web.server;

import java.util.Iterator;

import org.openelis.domain.PreferencesDO;
import org.openelis.gwt.common.Preferences;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PreferencesRemote;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PreferencesManager {
    
    public Preferences getSystem(Class c){
        return null;
    }
    
    public static Preferences getUser(Class c){
        return getUser(c.getName());
    }
    
    public static Preferences getUser(String key){
        PreferencesRemote remote = (PreferencesRemote)EJBFactory.lookup("openelis/PreferencesBean/remote");
        PreferencesDO prefDO = remote.getPreferences(key);
        Preferences prefs = new Preferences();
        prefs.setKey(prefDO.getKey());
        prefs.setId(prefDO.getId());
        try {
            Document prefDoc = XMLUtil.parse(prefDO.getText());
            NodeList nodes = prefDoc.getDocumentElement().getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++){
                Node node = nodes.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    if(node.getFirstChild() != null)
                        prefs.put(node.getNodeName(),node.getFirstChild().getNodeValue());
                }
            }
            return prefs;
        }catch(Exception e){
           e.printStackTrace();
        }
        return null;
        
        
    }
    
    public static void store(Preferences prefs){
        PreferencesRemote remote = (PreferencesRemote)EJBFactory.lookup("openelis/PreferencesBean/remote");
        PreferencesDO prefsDO = new PreferencesDO();
        prefsDO.setId(prefs.getId());
        prefsDO.setKey(prefs.getKey());
        try {
            Document prefsDoc = XMLUtil.createNew("preferences");
            Element root = prefsDoc.getDocumentElement();
            Iterator mapIt = prefs.getMap().keySet().iterator();
            while(mapIt.hasNext()){
                String key = (String)mapIt.next();
                String value = prefs.get(key);
                Element pref = prefsDoc.createElement(key);
                pref.appendChild(prefsDoc.createTextNode(value));
                root.appendChild(pref);
            }
            prefsDO.setText(XMLUtil.toString(prefsDoc));
            remote.setPreferences(prefsDO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
