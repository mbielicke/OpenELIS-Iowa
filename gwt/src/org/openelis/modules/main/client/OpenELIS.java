package org.openelis.modules.main.client;

import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.OptionField;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.QueryCheckField;
import org.openelis.gwt.common.data.QueryDateField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryOptionField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.screen.ScreenAToZPanel;
import org.openelis.gwt.screen.ScreenAbsolute;
import org.openelis.gwt.screen.ScreenAppButton;
import org.openelis.gwt.screen.ScreenAppMessage;
import org.openelis.gwt.screen.ScreenAuto;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenButtonPanel;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenDeck;
import org.openelis.gwt.screen.ScreenDragList;
import org.openelis.gwt.screen.ScreenDragSelect;
import org.openelis.gwt.screen.ScreenError;
import org.openelis.gwt.screen.ScreenHTML;
import org.openelis.gwt.screen.ScreenHorizontal;
import org.openelis.gwt.screen.ScreenHorizontalSplit;
import org.openelis.gwt.screen.ScreenImage;
import org.openelis.gwt.screen.ScreenLabel;
import org.openelis.gwt.screen.ScreenMaskedBox;
import org.openelis.gwt.screen.ScreenMenuBar;
import org.openelis.gwt.screen.ScreenMenuLabel;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenMenuPopupPanel;
import org.openelis.gwt.screen.ScreenOption;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenRadio;
import org.openelis.gwt.screen.ScreenStack;
import org.openelis.gwt.screen.ScreenTab;
import org.openelis.gwt.screen.ScreenTabBrowser;
import org.openelis.gwt.screen.ScreenTablePanel;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenText;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenTitledPanel;
import org.openelis.gwt.screen.ScreenTree;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindowBrowser;
import org.openelis.gwt.widget.HoverListener;
import org.openelis.gwt.widget.ProxyListener;
import org.openelis.gwt.widget.WidgetMap;
import org.openelis.gwt.widget.table.TableAuto;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableCalendar;
import org.openelis.gwt.widget.table.TableCheck;
import org.openelis.gwt.widget.table.TableCollection;
import org.openelis.gwt.widget.table.TableLabel;
import org.openelis.gwt.widget.table.TableMaskedTextBox;
import org.openelis.gwt.widget.table.TableOption;
import org.openelis.gwt.widget.table.TableTextBox;
//import org.openelis.modules.main.client.constants.OpenELISConstants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenELIS implements EntryPoint {
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  setWidgetMap();
      Window.addWindowCloseListener(new WindowCloseListener() {
          public String onWindowClosing() {
               org.openelis.modules.main.client.openelis.OpenELIS.screenService.logout(new AsyncCallback() {
                    public void onSuccess(Object result) {
                    }
                          
                    public void onFailure(Throwable caught) {
                    }
                });
                return null;
         }
                 
        public void onWindowClosed() {
                
        }
    });
    Window.enableScrolling(false);
	RootPanel.get("main").add(new org.openelis.modules.main.client.openelis.OpenELIS());
  }
  
  public void setWidgetMap() {
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_VERTICAL, new ScreenVertical());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_HORIZONTAL, new ScreenHorizontal());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_TABLE, new ScreenTablePanel());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_DECK, new ScreenDeck());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_HSPLIT, new ScreenHorizontalSplit());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_ABSOLUTE, new ScreenAbsolute());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.BUTTON, new ScreenAppButton());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.BUTTON_PANEL, new ScreenButtonPanel());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.CALENDAR, new ScreenCalendar());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.CHECKBOX, new ScreenCheck());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.DRAG_SELECT, new ScreenDragSelect());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.ERROR, new ScreenError());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.IMAGE, new ScreenImage());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.MASKED_BOX, new ScreenMaskedBox());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.OPTION_LIST, new ScreenOption());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RADIO_BUTTON, new ScreenRadio());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE, new ScreenTableWidget());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TEXT, new ScreenText());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TEXT_AREA, new ScreenTextArea());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TEXBOX, new ScreenTextBox());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TAB_BROWSER, new ScreenTabBrowser());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TREE, new ScreenTree());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_CALENDAR, new TableCalendar());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_LABEL, new TableLabel());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_MASKED_BOX, new TableMaskedTextBox());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_OPTION_LIST, new TableOption());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_TEXTBOX, new TableTextBox());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_AUTO, new TableAuto());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_AUTO_DROPDOWN, new TableAutoDropdown());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_COLLECTION, new TableCollection());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.DRAGLIST, new ScreenDragList());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.LABEL, new ScreenLabel());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.MENU_PANEL, new ScreenMenuPanel());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.MENU_LABEL, new ScreenMenuLabel());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.MENU_BAR, new ScreenMenuBar());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_CHECKBOX, new CheckField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_MODEL, new ModelField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_DATE, new DateField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_NUMBER, new NumberField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_OPTION, new OptionField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_STRING, new StringField());
      ScreenBase.getWidgetMap().addWidget("rpc-collection", new CollectionField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_TABLE, new TableField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_PAGED_TREE, new PagedTreeField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_QUERY_CHECK, new QueryCheckField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_QUERY_DATE, new QueryDateField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_QUERY_NUMBER, new QueryNumberField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_QUERY_OPTION, new QueryOptionField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.RPC_QUERY_STRING, new QueryStringField());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.TABLE_CHECKBOX, new TableCheck());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_TAB, new ScreenTab());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.PANEL_STACK, new ScreenStack());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.WINBROWSER, new ScreenWindowBrowser());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.APP_MESSAGE,new ScreenAppMessage());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.AUTO, new ScreenAuto());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.HTML, new ScreenHTML());
      ScreenBase.getWidgetMap().addWidget("ProxyListener", new ProxyListener());
      ScreenBase.getWidgetMap().addWidget("HoverListener", new HoverListener());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.LEFT_MENU_PANEL, new ScreenAToZPanel());
      ScreenBase.getWidgetMap().addWidget("titledPanel", new ScreenTitledPanel());
      ScreenBase.getWidgetMap().addWidget("menuPopupPanel", new ScreenMenuPopupPanel());
      //ScreenBase.getWidgetMap().addWidget("AppConstants", (OpenELISConstants)GWT.create(OpenELISConstants.class));
      ScreenBase.getWidgetMap().addWidget("pagedTree", new ScreenPagedTree());    
      ScreenBase.getWidgetMap().addWidget("appButton", new ScreenAppButton());
      ScreenBase.getWidgetMap().addWidget(WidgetMap.AUTO_DROPDOWN, new ScreenAutoDropdown());
  }
  
}
