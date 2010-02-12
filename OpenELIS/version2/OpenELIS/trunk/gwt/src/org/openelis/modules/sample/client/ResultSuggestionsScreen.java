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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.utilcommon.ResultValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class ResultSuggestionsScreen extends Screen implements HasActionHandlers<ResultSuggestionsScreen.Action>{
    public enum Action {OK};
    
    protected AutoComplete<String> suggestion;
    
    protected ResultValidator resultValidator;
    protected Integer unitId;
    protected String value;
     
     public ResultSuggestionsScreen() throws Exception {
         super((ScreenDefInt)GWT.create(ResultSuggestionsDef.class));

         // Setup link between Screen and widget Handlers
         initialize();

         // Initialize Screen
         setState(State.DEFAULT);
     }

     private void initialize() {
         suggestion = (AutoComplete<String>)def.getWidget("suggestion");
         addScreenHandler(suggestion, new ScreenEventHandler<String>() {
             public void onValueChange(ValueChangeEvent<String> event) {
                 value = event.getValue();
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 suggestion.enable(true);
             }
         });
         
         suggestion.addGetMatchesHandler(new GetMatchesHandler() {
                 public void onGetMatches(GetMatchesEvent event) {
                     String valueEntered;
                     TableDataRow row;
                     ArrayList<TableDataRow> model;
                     ArrayList<String> suggestions;
                     
                     valueEntered= event.getMatch();
                     
                     model = new ArrayList<TableDataRow>();
                     model.add(new TableDataRow(valueEntered, valueEntered));
                     
                     suggestions = resultValidator.getRanges(unitId);
                     for(int i=0; i<suggestions.size(); i++){
                         row = new TableDataRow(suggestions.get(i),suggestions.get(i));
                         row.enabled = false;
                         model.add(row);
                     }
                     
                     suggestions = resultValidator.getDictionaryRanges(unitId);
                     for(int i=0; i<suggestions.size(); i++)
                         model.add(new TableDataRow(suggestions.get(i),suggestions.get(i)));
                     
                     ((AutoComplete<String>)event.getSource()).showAutoMatches(model);
                 }
             });
             
         
         final AppButton okButton = (AppButton)def.getWidget("ok");
         addScreenHandler(okButton, new ScreenEventHandler<Object>() {
             public void onClick(ClickEvent event) {
                 ok();
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 okButton.enable(true);
             }
         });
         
         final AppButton cancelButton = (AppButton)def.getWidget("cancel");
         addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
             public void onClick(ClickEvent event) {
                 cancel();
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 cancelButton.enable(true);
             }
         });

     }
     
     private void ok(){
         window.close();
         ActionEvent.fire(this, Action.OK, value);
     }
     
     private void cancel(){
         window.close();
     }
     
     public void setValidator(ResultValidator resultValidator, Integer unitId, String currentValue){
         this.resultValidator = resultValidator;
         this.unitId = unitId;
         
         suggestion.setSelection(currentValue, currentValue);
         DeferredCommand.addCommand(new Command() {
             public void execute() {
                 setFocus(suggestion);
             }
         });
     }

     public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
         return addHandler(handler, ActionEvent.getType());
     }
 }
