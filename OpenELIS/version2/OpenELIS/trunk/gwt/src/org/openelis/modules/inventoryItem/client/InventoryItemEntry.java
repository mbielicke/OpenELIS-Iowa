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
package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class InventoryItemEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));

        ClassFactory.addClass(new String[] {"InventoryComponentsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new InventoryComponentsTable();
                                  }
                               }
       );
        
        ClassFactory.addClass(new String[] {"InventoryLocationsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new InventoryLocationsTable();
                                  }
                               }
       );
        
        ClassFactory.addClass(new String[] {"InventoryComponentAutoParams"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new InventoryComponentAutoParams();
                                  }
                               }
       );
        
        ClassFactory.addClass(new String[] {"InventoryItemScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new InventoryItemScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Inventory";
    }
}
