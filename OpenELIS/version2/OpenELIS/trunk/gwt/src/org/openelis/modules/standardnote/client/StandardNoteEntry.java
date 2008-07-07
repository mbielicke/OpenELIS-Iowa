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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.modules.standardnote.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class StandardNoteEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));
        ClassFactory.addClass(new String[] {"StandardNoteScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new StandardNoteScreen();
                                   }
                               }
        );
    }

    public String getModuleName() {
        return "StandardNote";
    }

}
