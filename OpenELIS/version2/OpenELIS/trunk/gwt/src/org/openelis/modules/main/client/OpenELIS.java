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
import org.openelis.gwt.screen.ScreenAppButton;
import org.openelis.gwt.screen.ScreenAppMessage;
import org.openelis.gwt.screen.ScreenAuto;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenButton;
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
import org.openelis.gwt.widget.table.TableLabel;
import org.openelis.gwt.widget.table.TableMaskedTextBox;
import org.openelis.gwt.widget.table.TableOption;
import org.openelis.gwt.widget.table.TableTextBox;
import org.openelis.modules.analysis.client.qaevent.QAEventsNamesTable;
import org.openelis.modules.dataEntry.client.Provider.ProviderAddressesTable;
import org.openelis.modules.dataEntry.client.Provider.ProviderNamesTable;
import org.openelis.modules.dataEntry.client.organization.OrganizationContactsTable;
import org.openelis.modules.dataEntry.client.organization.OrganizationNameTable;
import org.openelis.modules.main.client.constants.OpenELISConstants;
import org.openelis.modules.main.client.service.OpenELISService;
import org.openelis.modules.supply.client.storage.StorageNameTable;
import org.openelis.modules.supply.client.storageUnit.StorageUnitDescTable;
import org.openelis.modules.utilities.client.dictionary.CategorySystemNamesTable;
import org.openelis.modules.utilities.client.dictionary.DictionaryEntriesTable;

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
    Window.enableScrolling(false);
	RootPanel.get("main").add(new org.openelis.modules.main.client.openelis.OpenELIS());
  }
  
  public void setWidgetMap() {
      WidgetMap map = new WidgetMap();
      map.addWidget(WidgetMap.PANEL_VERTICAL, new ScreenVertical());
      map.addWidget(WidgetMap.PANEL_HORIZONTAL, new ScreenHorizontal());
      map.addWidget(WidgetMap.PANEL_TABLE, new ScreenTablePanel());
      map.addWidget(WidgetMap.PANEL_DECK, new ScreenDeck());
      map.addWidget(WidgetMap.PANEL_HSPLIT, new ScreenHorizontalSplit());
      map.addWidget(WidgetMap.BUTTON, new ScreenAppButton());
      map.addWidget(WidgetMap.BUTTON_PANEL, new ScreenButtonPanel());
      map.addWidget(WidgetMap.CALENDAR, new ScreenCalendar());
      map.addWidget(WidgetMap.CHECKBOX, new ScreenCheck());
      map.addWidget(WidgetMap.DRAG_SELECT, new ScreenDragSelect());
      map.addWidget(WidgetMap.ERROR, new ScreenError());
      map.addWidget(WidgetMap.IMAGE, new ScreenImage());
      map.addWidget(WidgetMap.MASKED_BOX, new ScreenMaskedBox());
      map.addWidget(WidgetMap.OPTION_LIST, new ScreenOption());
      map.addWidget(WidgetMap.RADIO_BUTTON, new ScreenRadio());
      map.addWidget(WidgetMap.TABLE, new ScreenTableWidget());
      map.addWidget(WidgetMap.TEXT, new ScreenText());
      map.addWidget(WidgetMap.TEXT_AREA, new ScreenTextArea());
      map.addWidget(WidgetMap.TEXBOX, new ScreenTextBox());
      map.addWidget(WidgetMap.TAB_BROWSER, new ScreenTabBrowser());
      map.addWidget(WidgetMap.TREE, new ScreenTree());
      map.addWidget(WidgetMap.TABLE_CALENDAR, new TableCalendar());
      map.addWidget(WidgetMap.TABLE_LABEL, new TableLabel());
      map.addWidget(WidgetMap.TABLE_MASKED_BOX, new TableMaskedTextBox());
      map.addWidget(WidgetMap.TABLE_OPTION_LIST, new TableOption());
      map.addWidget(WidgetMap.TABLE_TEXTBOX, new TableTextBox());
      map.addWidget(WidgetMap.TABLE_AUTO, new TableAuto());
      map.addWidget(WidgetMap.TABLE_AUTO_DROPDOWN, new TableAutoDropdown());
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
      map.addWidget(WidgetMap.RPC_MODEL, new ModelField());
      map.addWidget(WidgetMap.RPC_DATE, new DateField());
      map.addWidget(WidgetMap.RPC_NUMBER, new NumberField());
      map.addWidget(WidgetMap.RPC_OPTION, new OptionField());
      map.addWidget(WidgetMap.RPC_STRING, new StringField());
      map.addWidget("rpc-collection", new CollectionField());
      map.addWidget(WidgetMap.RPC_TABLE, new TableField());
      map.addWidget(WidgetMap.RPC_PAGED_TREE, new PagedTreeField());
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
      map.addWidget(WidgetMap.AUTO, new ScreenAuto());
      map.addWidget(WidgetMap.HTML, new ScreenHTML());
      map.addWidget("ProxyListener", new ProxyListener());
      map.addWidget("HoverListener", new HoverListener());
      map.addWidget(WidgetMap.LEFT_MENU_PANEL, new ScreenAToZPanel());
      map.addWidget("titledPanel", new ScreenTitledPanel());
      map.addWidget("menuPopupPanel", new ScreenMenuPopupPanel());
      map.addWidget("AppConstants", (OpenELISConstants)GWT.create(OpenELISConstants.class));
      map.addWidget("OpenELISService",OpenELISService.getInstance());
      map.addWidget("OrganizationNameTable", new OrganizationNameTable());

      map.addWidget("OrganizationContactsTable", new OrganizationContactsTable());
      map.addWidget("ProviderNamesTable", new ProviderNamesTable());
      map.addWidget("ProviderAddressesTable", new ProviderAddressesTable());
      map.addWidget("CategorySystemNamesTable", new CategorySystemNamesTable());
      map.addWidget("pagedTree", new ScreenPagedTree());    
      map.addWidget("appButton", new ScreenAppButton());
      map.addWidget("DictionaryEntriesTable", new DictionaryEntriesTable());
      map.addWidget("QAEventsNamesTable", new QAEventsNamesTable()); 
      map.addWidget(WidgetMap.AUTO_DROPDOWN, new ScreenAutoDropdown()); 
	  ScreenBase.setWidgetMap(map);
  }
}
