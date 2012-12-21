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
package org.openelis.modules.qaevent.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class QaeventLookupScreen extends Screen implements HasActionHandlers<QaeventLookupScreen.Action> {
	public enum Action {
		OK
	};

	public enum Type {
		SAMPLE, ANALYSIS
	};

	protected Integer testId;
	protected Type type;

	private AppButton okButton, cancelButton;
	private TableWidget qaEventTable;
	private ArrayList<TableDataRow> model;

	public QaeventLookupScreen() throws Exception {
	
		super((ScreenDefInt) GWT.create(QaeventLookupDef.class));

		// Setup link between Screen and widget Handlers
		initialize();
		initializeDropdowns();

		// Initialize Screen
		setState(State.DEFAULT);
	}

	/**
	 * Method needs to be called to refresh the table based on the
	 * specified type and test.
	 */
	public void draw() {
		TableDataRow row;
		ArrayList<QaEventDO> list;

		try {
			if (type == Type.ANALYSIS && testId != null)
				list = QaEventService.get().fetchByTestId(testId);
			else
				list = QaEventService.get().fetchByCommon();

			/*
			 * load the model
			 */
			model = new ArrayList<TableDataRow>();
			for (QaEventDO data: list) {
				row = new TableDataRow(data.getId(), data.getName(), data.getDescription(),
									   data.getTypeId(), data.getIsBillable());
				row.data = data;
				model.add(row);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			Window.alert(e.getMessage());
			model.clear();
		}

		DataChangeEvent.fire(this);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
	}

	private void initialize() {
		qaEventTable = (TableWidget) def.getWidget("qaEventTable");
		addScreenHandler(qaEventTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
			public void onDataChange(DataChangeEvent event) {
				qaEventTable.load(getTableModel());
			}

			public void onStateChange(StateChangeEvent<State> event) {
				qaEventTable.enable(EnumSet.of(State.QUERY, State.ADD,
						State.UPDATE).contains(event.getState()));
				qaEventTable.setQueryMode(event.getState() == State.QUERY);
			}
		});

		qaEventTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
			public void onBeforeSelection(
					BeforeSelectionEvent<TableRow> event) {
				// do nothing
			};
		});

		qaEventTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
			public void onBeforeCellEdited(BeforeCellEditedEvent event) {
				event.cancel();
			}
		});

		okButton = (AppButton) def.getWidget("ok");
		addScreenHandler(okButton, new ScreenEventHandler<Object>() {
			public void onClick(ClickEvent event) {
				if (okButton.isEnabled())
					ok();
			}

			public void onStateChange(StateChangeEvent<State> event) {
				okButton.enable(true);
			}
		});

		cancelButton = (AppButton) def.getWidget("cancel");
		addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
			public void onClick(ClickEvent event) {
				if (cancelButton.isEnabled())
					cancel();
			}

			public void onStateChange(StateChangeEvent<State> event) {
				cancelButton.enable(true);
			}
		});
	}

	private void initializeDropdowns() {
		ArrayList<TableDataRow> model;

		model = new ArrayList<TableDataRow>();
		model.add(new TableDataRow(null, ""));
		for (DictionaryDO d : CategoryCache.getBySystemName("qaevent_type"))
			model.add(new TableDataRow(d.getId(), d.getEntry()));

		((Dropdown<Integer>) qaEventTable.getColumns().get(2).getColumnWidget()).setModel(model);
	}

	private void ok() {
		ArrayList<TableDataRow> list;
		
		list = qaEventTable.getSelections();
		if (list.size() > 0)
			ActionEvent.fire(this, Action.OK, list);

		window.close();
	}

	private void cancel() {
		window.close();
	}

	private ArrayList<TableDataRow> getTableModel() {
		return model;
	}

	public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
		return addHandler(handler, ActionEvent.getType());
	}
}
