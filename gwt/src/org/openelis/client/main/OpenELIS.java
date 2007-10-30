package org.openelis.client.main;

import org.openelis.client.main.constants.OpenELISConstants;
import org.openelis.client.main.service.OpenELISService;
import org.openelis.gwt.client.screen.Screen;
import org.openelis.gwt.client.screen.ScreenAToZPanel;
import org.openelis.gwt.client.screen.ScreenAppMessage;
import org.openelis.gwt.client.screen.ScreenButton;
import org.openelis.gwt.client.screen.ScreenButtonPanel;
import org.openelis.gwt.client.screen.ScreenCalendar;
import org.openelis.gwt.client.screen.ScreenCheck;
import org.openelis.gwt.client.screen.ScreenConstant;
import org.openelis.gwt.client.screen.ScreenDeck;
import org.openelis.gwt.client.screen.ScreenDragList;
import org.openelis.gwt.client.screen.ScreenDragSelect;
import org.openelis.gwt.client.screen.ScreenError;
import org.openelis.gwt.client.screen.ScreenHTML;
import org.openelis.gwt.client.screen.ScreenHorizontal;
import org.openelis.gwt.client.screen.ScreenHorizontalSplit;
import org.openelis.gwt.client.screen.ScreenImage;
import org.openelis.gwt.client.screen.ScreenLabel;
import org.openelis.gwt.client.screen.ScreenMaskedBox;
import org.openelis.gwt.client.screen.ScreenMenuBar;
import org.openelis.gwt.client.screen.ScreenMenuLabel;
import org.openelis.gwt.client.screen.ScreenMenuPanel;
import org.openelis.gwt.client.screen.ScreenMenuPopupPanel;
import org.openelis.gwt.client.screen.ScreenOption;
import org.openelis.gwt.client.screen.ScreenRadio;
import org.openelis.gwt.client.screen.ScreenStack;
import org.openelis.gwt.client.screen.ScreenTab;
import org.openelis.gwt.client.screen.ScreenTabBrowser;
import org.openelis.gwt.client.screen.ScreenTable;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.screen.ScreenText;
import org.openelis.gwt.client.screen.ScreenTextArea;
import org.openelis.gwt.client.screen.ScreenTextBox;
import org.openelis.gwt.client.screen.ScreenTitledPanel;
import org.openelis.gwt.client.screen.ScreenTree;
import org.openelis.gwt.client.screen.ScreenVertical;
import org.openelis.gwt.client.screen.ScreenWindowBrowser;
import org.openelis.gwt.client.widget.HoverListener;
import org.openelis.gwt.client.widget.ProxyListener;
import org.openelis.gwt.client.widget.WidgetMap;
import org.openelis.gwt.client.widget.table.TableCalendar;
import org.openelis.gwt.client.widget.table.TableCheck;
import org.openelis.gwt.client.widget.table.TableLabel;
import org.openelis.gwt.client.widget.table.TableLink;
import org.openelis.gwt.client.widget.table.TableMaskedTextBox;
import org.openelis.gwt.client.widget.table.TableOption;
import org.openelis.gwt.client.widget.table.TableTextBox;
import org.openelis.gwt.common.CheckField;
import org.openelis.gwt.common.DateField;
import org.openelis.gwt.common.NumberField;
import org.openelis.gwt.common.OptionField;
import org.openelis.gwt.common.QueryCheckField;
import org.openelis.gwt.common.QueryDateField;
import org.openelis.gwt.common.QueryNumberField;
import org.openelis.gwt.common.QueryOptionField;
import org.openelis.gwt.common.QueryStringField;
import org.openelis.gwt.common.StringField;
import org.openelis.gwt.common.TableField;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenELIS implements EntryPoint {
    {
        OpenELISService.init();
    }
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  setWidgetMap();
      Window.addWindowCloseListener(new WindowCloseListener() {
          public String onWindowClosing() {
               OpenELISService.getInstance().logout(new AsyncCallback() {
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
    Window.enableScrolling(true);
	RootPanel.get("main").add(new org.openelis.client.main.screen.OpenELIS());
  }
  
  private void setWidgetMap() {
      WidgetMap map = new WidgetMap();
      map.addWidget(WidgetMap.PANEL_VERTICAL, new ScreenVertical());
      map.addWidget(WidgetMap.PANEL_HORIZONTAL, new ScreenHorizontal());
      map.addWidget(WidgetMap.PANEL_TABLE, new ScreenTablePanel());
      map.addWidget(WidgetMap.PANEL_DECK, new ScreenDeck());
      map.addWidget(WidgetMap.PANEL_HSPLIT, new ScreenHorizontalSplit());
      map.addWidget(WidgetMap.BUTTON, new ScreenButton());
      map.addWidget(WidgetMap.BUTTON_PANEL, new ScreenButtonPanel());
      map.addWidget(WidgetMap.CALENDAR, new ScreenCalendar());
      map.addWidget(WidgetMap.CHECKBOX, new ScreenCheck());
      map.addWidget(WidgetMap.DRAG_SELECT, new ScreenDragSelect());
      map.addWidget(WidgetMap.ERROR, new ScreenError());
      map.addWidget(WidgetMap.IMAGE, new ScreenImage());
      map.addWidget(WidgetMap.MASKED_BOX, new ScreenMaskedBox());
      map.addWidget(WidgetMap.OPTION_LIST, new ScreenOption());
      map.addWidget(WidgetMap.RADIO_BUTTON, new ScreenRadio());
      map.addWidget(WidgetMap.TABLE, new ScreenTable());
      map.addWidget(WidgetMap.TEXT, new ScreenText());
      map.addWidget(WidgetMap.TEXT_AREA, new ScreenTextArea());
      map.addWidget(WidgetMap.TEXBOX, new ScreenTextBox());
      map.addWidget(WidgetMap.TAB_BROWSER, new ScreenTabBrowser());
      map.addWidget(WidgetMap.TREE, new ScreenTree());
      map.addWidget(WidgetMap.TABLE_CALENDAR, new TableCalendar());
      map.addWidget(WidgetMap.TABLE_LABEL, new TableLabel());
      map.addWidget(WidgetMap.TABLE_LINK, new TableLink());
      map.addWidget(WidgetMap.TABLE_MASKED_BOX, new TableMaskedTextBox());
      map.addWidget(WidgetMap.TABLE_OPTION_LIST, new TableOption());
      map.addWidget(WidgetMap.TABLE_TEXTBOX, new TableTextBox());
      map.addWidget(WidgetMap.DRAGLIST, new ScreenDragList());
      map.addWidget(WidgetMap.LABEL, new ScreenLabel());
     // map.addWidget("menuList", new ScreenMenuPanel());
      map.addWidget(WidgetMap.MENU_PANEL, new ScreenMenuPanel());
      map.addWidget(WidgetMap.MENU_LABEL, new ScreenMenuLabel());
      map.addWidget(WidgetMap.MENU_BAR, new ScreenMenuBar());
     // map.addWidget("OrganizationTable", new OrganizationTable());
     // map.addWidget("ContactTable", new ContactTable());
      //map.addWidget("ContactReportingTable", new ContactReportingTable());
      map.addWidget(WidgetMap.RPC_CHECKBOX, new CheckField());
      map.addWidget(WidgetMap.RPC_DATE, new DateField());
      map.addWidget(WidgetMap.RPC_NUMBER, new NumberField());
      map.addWidget(WidgetMap.RPC_OPTION, new OptionField());
      map.addWidget(WidgetMap.RPC_STRING, new StringField());
      map.addWidget(WidgetMap.RPC_TABLE, new TableField());
      map.addWidget(WidgetMap.RPC_QUERY_CHECK, new QueryCheckField());
      map.addWidget(WidgetMap.RPC_QUERY_DATE, new QueryDateField());
      map.addWidget(WidgetMap.RPC_QUERY_NUMBER, new QueryNumberField());
      map.addWidget(WidgetMap.RPC_QUERY_OPTION, new QueryOptionField());
      map.addWidget(WidgetMap.RPC_QUERY_STRING, new QueryStringField());
      map.addWidget(WidgetMap.TABLE_CHECKBOX, new TableCheck());
      map.addWidget(WidgetMap.PANEL_TAB, new ScreenTab());
      map.addWidget(WidgetMap.PANEL_STACK, new ScreenStack());
      map.addWidget(WidgetMap.WINBROWSER, new ScreenWindowBrowser());
      map.addWidget(WidgetMap.APP_MESSAGE,new ScreenAppMessage());
      map.addWidget(WidgetMap.HTML, new ScreenHTML());
      map.addWidget("ProxyListener", new ProxyListener());
      map.addWidget("HoverListener", new HoverListener());
      map.addWidget(WidgetMap.LEFT_MENU_PANEL, new ScreenAToZPanel());
      map.addWidget("titledPanel", new ScreenTitledPanel());
      map.addWidget("menuPopupPanel", new ScreenMenuPopupPanel());
      map.addWidget(WidgetMap.CONTSTANT, new ScreenConstant());
      map.addWidget("OpenELISConstants", (OpenELISConstants)GWT.create(OpenELISConstants.class));
      map.addWidget("OpenELISService",OpenELISService.getAppServInstance());
	  Screen.setWidgetMap(map);
  }
}
