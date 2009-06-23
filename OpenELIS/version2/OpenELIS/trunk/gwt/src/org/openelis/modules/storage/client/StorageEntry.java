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
package org.openelis.modules.storage.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

public class StorageEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.add(getModuleName());
        ClassFactory.addScreen("StorageLocationScreen", 
                new ClassFactory.ShowScreen() {
                    public void showScreen(Object[] args) {
                 	   GWT.runAsync(new RunAsyncCallback() {
                 		   public void onSuccess() {
                 			   OpenELIS.browser.addScreen(new StorageLocationScreen());
                 		   }
                 		   
                 		   public void onFailure(Throwable caught) {
                 			   
                 		   }
                 	   }); 
                        
                    }
                 }
);
    }

    public String getModuleName() {
        return "Storage";
    }

}
