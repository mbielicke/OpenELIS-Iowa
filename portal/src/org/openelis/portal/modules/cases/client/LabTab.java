package org.openelis.portal.modules.cases.client;

import org.openelis.portal.client.resources.Resources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.columnar.Columnar;
import org.openelis.ui.widget.columnar.DataItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class LabTab extends Screen {
	
	@UiTemplate("Lab.ui.xml")
	interface LabUiBinder extends UiBinder<Widget,LabTab>{};
	private static final LabUiBinder uiBinder = GWT.create(LabUiBinder.class);
	
	@UiField
	DivElement results;
	
	public LabTab() {
		initWidget(uiBinder.createAndBindUi(this));
	
		DivElement column = Document.get().createDivElement();
		column.addClassName(Resources.INSTANCE.style().Column());
		
		column.appendChild(createResult("184789"));
		
		column.appendChild(createResult("Yes(2012-07-13"));
		column.appendChild(createResult("NPO"));
		column.appendChild(createResult("Within Normal Limits"));
		column.appendChild(createResult("Within Normal Limits"));
		column.appendChild(createResult("Transfused"));
		column.appendChild(createResult("Transfused"));
		column.appendChild(createResult("Transfused"));
		column.appendChild(createResult("Transfused"));
		column.appendChild(createResult("Transfused"));
		column.appendChild(createResult("Within Normal Limits"));
		
		results.appendChild(column);
	}

	
	private DivElement createResult(String result) {
		DivElement div = Document.get().createDivElement();
		div.setInnerText(result);
		return div;
	}
}
