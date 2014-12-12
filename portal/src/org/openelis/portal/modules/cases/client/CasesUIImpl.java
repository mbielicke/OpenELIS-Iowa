package org.openelis.portal.modules.cases.client;

import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class CasesUIImpl extends ResizeComposite implements CasesUI {

	@UiTemplate("Cases.ui.xml")
	interface CasesUiBinder extends UiBinder<Widget,CasesUIImpl>{};
	private static final CasesUiBinder uiBinder = GWT.create(CasesUiBinder.class);
	
	@UiField
	Table cases, tagTable;
	
	@UiField
	Dropdown<Integer> tags;
	
	
	public CasesUIImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public Table getCases() {
		return cases;
	}
	
	@Override 
	public Dropdown<Integer> tags() {
		return tags;
	}
	
	@Override 
	public Table tagTable() {
		return tagTable;
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
	
	
}
