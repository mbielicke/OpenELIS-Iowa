package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.screen.AppScreen;
import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.screen.ScreenAToZPanel;
import org.openelis.gwt.client.screen.ScreenLabel;
import org.openelis.gwt.client.screen.ScreenPagedTree;
import org.openelis.gwt.client.screen.ScreenTableWidget;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.screen.ScreenTextBox;
import org.openelis.gwt.client.widget.Button;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.TableWidget;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class Organization extends AppScreenForm {

	private ConstantsWithLookup openElisConstants = (ConstantsWithLookup) AppScreen
			.getWidgetMap().get("AppConstants");

	private static OrganizationServletIntAsync screenService = (OrganizationServletIntAsync) GWT
			.create(OrganizationServletInt.class);

	private static ServiceDefTarget target = (ServiceDefTarget) screenService;

	private int tabSelectedIndex = 0;

	private Widget selected;

	Document xml = null;

	public Organization() {
		super();
		String base = GWT.getModuleBaseURL();
		base += "OrganizationServlet";
		target.setServiceEntryPoint(base);
		service = screenService;
		formService = screenService;
		getXML();
	}

	public void onClick(Widget sender) {
		if (sender == widgets.get("addButton")) {
			ScreenTablePanel tp = (ScreenTablePanel) widgets
					.get("noteFormPanel");
			tp.getWidget().setVisible(true);
			// }else if (sender == widgets.get("lookupParentOrganizationHtml")){
			// new OrganizationChoose();
		} else if (sender == widgets.get("openSidePanelButton")) {
			HorizontalPanel hp = (HorizontalPanel) getWidget("leftPanel");
			if (hp.isVisible()) {
				hp.setVisible(false);
				// HTML html = new HTML("<img
				// src=\"Images/close_left_panel.png\">");
				HTML screenHtml = (HTML) getWidget("openSidePanelButton");
				screenHtml
						.setHTML("<img src=\"Images/arrow-right-unselected.png\" onmouseover=\"this.src='Images/arrow-right-selected.png';\" onmouseout=\"this.src='Images/arrow-right-unselected.png';\">");
				// screenHtml.initWidget(html);
				// html.setStyleName("ScreenHTML");
			} else {
				hp.setVisible(true);
				HTML screenHtml = (HTML) getWidget("openSidePanelButton");
				screenHtml
						.setHTML("<img src=\"Images/arrow-left-unselected.png\" onmouseover=\"this.src='Images/arrow-left-selected.png';\" onmouseout=\"this.src='Images/arrow-left-unselected.png';\">");
			}
		}

		if (sender == widgets.get("a")) {
			getOrganizations("a", sender);
		} else if (sender == widgets.get("b")) {
			getOrganizations("b", sender);
		} else if (sender == widgets.get("c")) {
			getOrganizations("c", sender);
		} else if (sender == widgets.get("d")) {
			getOrganizations("d", sender);
		} else if (sender == widgets.get("e")) {
			getOrganizations("e", sender);
		} else if (sender == widgets.get("f")) {
			getOrganizations("f", sender);
		} else if (sender == widgets.get("g")) {
			getOrganizations("g", sender);
		} else if (sender == widgets.get("h")) {
			getOrganizations("h", sender);
		} else if (sender == widgets.get("i")) {
			getOrganizations("i", sender);
		} else if (sender == widgets.get("j")) {
			getOrganizations("j", sender);
		} else if (sender == widgets.get("k")) {
			getOrganizations("k", sender);
		} else if (sender == widgets.get("l")) {
			getOrganizations("l", sender);
		} else if (sender == widgets.get("m")) {
			getOrganizations("m", sender);
		} else if (sender == widgets.get("n")) {
			getOrganizations("n", sender);
		} else if (sender == widgets.get("o")) {
			getOrganizations("o", sender);
			setStyleNameOnButton(sender);
		} else if (sender == widgets.get("p")) {
			getOrganizations("p", sender);
		} else if (sender == widgets.get("q")) {
			getOrganizations("q", sender);
		} else if (sender == widgets.get("r")) {
			getOrganizations("r", sender);
		} else if (sender == widgets.get("s")) {
			getOrganizations("s", sender);
		} else if (sender == widgets.get("t")) {
			getOrganizations("t", sender);
		} else if (sender == widgets.get("u")) {
			getOrganizations("u", sender);
		} else if (sender == widgets.get("v")) {
			getOrganizations("v", sender);
		} else if (sender == widgets.get("w")) {
			getOrganizations("w", sender);
		} else if (sender == widgets.get("x")) {
			getOrganizations("x", sender);
		} else if (sender == widgets.get("y")) {
			getOrganizations("y", sender);
		} else if (sender == widgets.get("z")) {
			getOrganizations("z", sender);
		} else if (sender == widgets.get("removeContactButton")) {
			TableWidget orgContactsTable = (TableWidget) getWidget("contactsTable");
			int selectedRow = orgContactsTable.controller.selected;
			if (selectedRow > -1
					&& orgContactsTable.controller.model.numRows() > 1) {
				TableRow row = orgContactsTable.controller.model
						.getRow(selectedRow);
				row.setShow(false);
				// delete the last row of the table because it is autoadd
				orgContactsTable.controller.model
						.deleteRow(orgContactsTable.controller.model.numRows() - 1);
				// reset the model
				orgContactsTable.controller.reset();
				// need to set the deleted flag to "Y" also
				StringField deleteFlag = new StringField();
				deleteFlag.setValue("Y");

				row.addHidden("deleteFlag", deleteFlag);
			}

		} else if (sender == ((ScreenAToZPanel) widgets.get("hideablePanel")).div) {
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					ScreenAToZPanel panel = (ScreenAToZPanel) widgets
							.get("hideablePanel");

					// get the selected row
					int selectedRow = ((TableWidget) getWidget("organizationsTable")).controller.selected;
					// only need to reset the table if the user is opening the
					// panel
					if (panel.panelOpen())
						((TableWidget) getWidget("organizationsTable")).controller
								.reset();

				}
			});

		}
	}

	public void afterDraw(boolean success) {

		bpanel = (ButtonPanel) getWidget("buttons");

		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = true;

		Button removeContactButton = (Button) getWidget("removeContactButton");
		removeContactButton.setEnabled(false);

		TableWidget orgNameTable = (TableWidget) getWidget("organizationsTable");
		modelWidget.addChangeListener(orgNameTable.controller);

		// if(constants != null)
		// message.setText(openElisConstants.getString("loadCompleteMessage"));
		// else
		message.setText("done");

		// get contacts table and set the managers form
		TableWidget contactsTable = (TableWidget) getWidget("contactsTable");
		((OrganizationContactsTable) contactsTable.controller.manager)
				.setOrganizationForm(this);

		// get contacts table and set the managers form

		((OrganizationNameTable) orgNameTable.controller.manager)
				.setOrganizationForm(this);

		super.afterDraw(success);
	}

	public void afterFetch(boolean success) {
		super.afterFetch(success);
		
		FormRPC displayRPC = (FormRPC) this.forms.get("display");
		DataModel notesModel = (DataModel)displayRPC.getFieldValue("notesModel");

		VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");
		
		//we need to remove anything in the notes tab if it exists
		vp.clear();
		int i=0;
		while(notesModel != null && i<notesModel.size()){
			HorizontalPanel subjectPanel = new HorizontalPanel();
			HorizontalPanel spacerPanel = new HorizontalPanel();
			HorizontalPanel bodyPanel = new HorizontalPanel();
			
			Label subjectLabel = new Label();
			Label bodyLabel = new Label();
			
			vp.add(subjectPanel);
			vp.add(bodyPanel);
			subjectPanel.add(subjectLabel);
			bodyPanel.add(spacerPanel);
			bodyPanel.add(bodyLabel);			
			
			spacerPanel.setWidth("25px");
			subjectPanel.setWidth("100%");
			bodyPanel.setWidth("100%");
			
			subjectLabel.addStyleName("NotesText");
			bodyLabel.addStyleName("NotesText");
			
			subjectLabel.setText((String)notesModel.get(i).getObject(0).getValue());
			bodyLabel.setText((String)notesModel.get(i).getObject(1).getValue());
			
			i++;
		}
	}
	private void getOrganizations(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (bpanel.getState() == FormInt.DISPLAY) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			letterRPC.setFieldValue("orgName", letter.toUpperCase() + "*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}

	public void onTabSelected(SourcesTabEvents sources, int index) {
		tabSelectedIndex = index;
		// we need to do a org contacts table reset so that it will always show
		// the data
		if (index == 0 && bpanel.getState() == FormInt.DISPLAY) {
			TableWidget contacts = (TableWidget) getWidget("contactsTable");
			contacts.controller.model.deleteRow(contacts.controller.model
					.numRows() - 1);
			contacts.controller.reset();
		}
		super.onTabSelected(sources, index);
	}

	// button panel action methods
	public void add(int state) {
		super.add(state);
		ScreenTextBox orgId = (ScreenTextBox) widgets.get("orgId");
		orgId.enable(false);

		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = false;

		// unselect the row from the table
		((TableWidget) getWidget("organizationsTable")).controller.unselect(-1);

		Button removeContactButton = (Button) getWidget("removeContactButton");
		removeContactButton.setEnabled(true);

		TableWidget contactsTable = (TableWidget) getWidget("contactsTable");
	}

	public void abort(int state) {
		TableWidget orgContacts = (TableWidget) getWidget("contactsTable");
		// need to remove the extra table row on update
		if (bpanel.getState() == FormInt.UPDATE)
			orgContacts.controller.model.deleteRow(orgContacts.controller.model
					.numRows() - 1);

		super.abort(state);

		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) orgContacts.controller.manager;
		orgContactsTable.disableRows = true;
		orgContacts.controller.unselect(-1);

		Button removeContactButton = (Button) getWidget("removeContactButton");
		removeContactButton.setEnabled(false);

		// need to get the org name table model
		TableWidget orgNameTM = (TableWidget) getWidget("organizationsTable");
		int rowSelected = orgNameTM.controller.selected;

		// set the update button if needed
		if (rowSelected != -1)
			bpanel.enable("u", true);
		
		//after update we need to enable the next previous buttons
		
		//after query we need to enable the next previous buttons
	}

	public void up(int state) {
		super.up(state);
	}

	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		ScreenTextBox orgId = (ScreenTextBox) widgets.get("orgId");
		orgId.enable(false);

		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = false;

		Button removeContactButton = (Button) getWidget("removeContactButton");
		removeContactButton.setEnabled(true);
	}

	public void next(int state) {
		super.next(state);
	}

	public void prev(int state) {
		super.prev(state);
	}

	public void query(int state) {
		super.query(state);
	}

	public void afterCommitQuery(Object field, boolean success) {
		super.afterCommitQuery(success);

	}

	public void afterCommitAdd(boolean success) {
		Button removeContactButton = (Button) getWidget("removeContactButton");
		removeContactButton.setEnabled(true);

		super.afterCommitAdd(success);
	}

	public void afterCommitUpdate(boolean success) {

		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = true;

		Button removeContactButton = (Button) getWidget("removeContactButton");
		removeContactButton.setEnabled(false);

		super.afterCommitUpdate(success);
	}

	public void afterDelete(boolean success) {
		// TODO Auto-generated method stub
		super.afterDelete(success);
	}

	public void commit(int state) {
		if (state == FormInt.QUERY) {
			((TableWidget) ((ScreenTableWidget) ((ScreenTableWidget) widgets
					.get("contactsTable")).getQueryWidget()).getWidget()).controller
					.unselect(-1);
		} else {
			((TableWidget) getWidget("contactsTable")).controller.unselect(-1);
		}
		super.commit(state);
	}

	protected Widget setStyleNameOnButton(Widget sender) {
		sender.addStyleName("current");
		if (selected != null)
			selected.removeStyleName("current");
		selected = sender;
		return sender;
	}
}
