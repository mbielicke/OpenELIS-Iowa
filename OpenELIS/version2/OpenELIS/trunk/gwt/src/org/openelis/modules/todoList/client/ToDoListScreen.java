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
package org.openelis.modules.todoList.client;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class ToDoListScreen extends Screen {
    
    private Dropdown<Integer>      sampleDomain;
    
    public ToDoListScreen() {
        super((ScreenDefInt)GWT.create(ToDoListDef.class));

        // if (userPermission == null)
        // throw new PermissionException("screenPermException",
        // "Oranization Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {
        //tab = Tabs.CONTACT;
        //manager = OrganizationManager.getInstance();

        /*try {
            CategoryCache.getBySystemNames("country", "state",
                                       "contact_type", "parameter_type");
        } catch (Exception e) {
            Window.alert("OrganizationScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }*/
    
        initialize();
        //setState(State.DEFAULT);
        //initializeDropdowns();
        //DataChangeEvent.fire(this);
    }

    private void initialize() {
        // TODO Auto-generated method stub

    }
}
