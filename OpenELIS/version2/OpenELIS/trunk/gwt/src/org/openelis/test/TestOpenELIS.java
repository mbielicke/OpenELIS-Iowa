package org.openelis.test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import org.openelis.client.analysis.screen.qaevent.QAEventsNamesTable;
import org.openelis.client.dataEntry.screen.Provider.ProviderAddressesTable;
import org.openelis.client.dataEntry.screen.Provider.ProviderNamesTable;
import org.openelis.client.dataEntry.screen.organization.OrganizationContactsTable;
import org.openelis.client.dataEntry.screen.organization.OrganizationNameTable;
import org.openelis.client.dataEntry.screen.organization.OrganizationScreen;
import org.openelis.client.main.constants.OpenELISConstants;
import org.openelis.client.main.service.OpenELISService;
import org.openelis.client.supply.screen.storage.StorageNameTable;
import org.openelis.client.supply.screen.storageUnit.StorageUnitDescTable;
import org.openelis.client.utilities.screen.dictionary.CategorySystemNamesTable;
import org.openelis.client.utilities.screen.dictionary.DictionaryEntriesTable;
import org.openelis.gwt.client.screen.ScreenAToZPanel;
import org.openelis.gwt.client.screen.ScreenAppButton;
import org.openelis.gwt.client.screen.ScreenAppMessage;
import org.openelis.gwt.client.screen.ScreenAuto;
import org.openelis.gwt.client.screen.ScreenAutoDropdown;
import org.openelis.gwt.client.screen.ScreenBase;
import org.openelis.gwt.client.screen.ScreenButtonPanel;
import org.openelis.gwt.client.screen.ScreenCalendar;
import org.openelis.gwt.client.screen.ScreenCheck;
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
import org.openelis.gwt.client.screen.ScreenPagedTree;
import org.openelis.gwt.client.screen.ScreenRadio;
import org.openelis.gwt.client.screen.ScreenStack;
import org.openelis.gwt.client.screen.ScreenTab;
import org.openelis.gwt.client.screen.ScreenTabBrowser;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.screen.ScreenTableWidget;
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
import org.openelis.gwt.client.widget.table.TableAuto;
import org.openelis.gwt.client.widget.table.TableAutoDropdown;
import org.openelis.gwt.client.widget.table.TableCalendar;
import org.openelis.gwt.client.widget.table.TableCheck;
import org.openelis.gwt.client.widget.table.TableLabel;
import org.openelis.gwt.client.widget.table.TableMaskedTextBox;
import org.openelis.gwt.client.widget.table.TableOption;
import org.openelis.gwt.client.widget.table.TableTextBox;
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

public class TestOpenELIS extends GWTTestCase {
    public TestOpenELIS() {
        super();
        

        }
    
    public String getModuleName() {
        return "org.openelis.TestOpenELIS";
    }
    
    public void testOrganizationScreen() {
        OpenELISService.init();
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
        map.addWidget("StorageNameTable", new StorageNameTable());
        map.addWidget("StorageUnitDescTable", new StorageUnitDescTable());
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
        System.out.println("Creating org screen");
        final OrganizationScreen orgScreen = new OrganizationScreen();
        Timer timer = new Timer() {
            public void run() {
                assertNotNull(orgScreen.forms.get("display"));
                finishTest();
            }
        };
        assertNotNull(orgScreen);
        delayTestFinish(10000);
        timer.schedule(5000);
    }
    

}
