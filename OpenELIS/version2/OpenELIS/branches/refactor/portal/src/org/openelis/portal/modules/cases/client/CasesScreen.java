package org.openelis.portal.modules.cases.client;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.domain.DictionaryDO;
import org.openelis.portal.cache.CategoryCache;
import org.openelis.portal.cache.DictionaryCache;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.client.Logger;
import org.openelis.portal.client.OpenELISPortalEntry;
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.cell.CheckLabelValue;
import org.openelis.ui.widget.table.Row;

import com.google.gwt.aria.client.CheckedValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;

public class CasesScreen extends Screen {
	
	CasesUI ui = GWT.create(CasesUIImpl.class);
	CasesService service = CasesService.INSTANCE;
	
	public CasesScreen() throws Exception {
		initWidget(ui.asWidget());
		initialize();
	}
	
	protected void initialize() throws Exception{
		service.getActiveCases(new AsyncCallbackUI<ArrayList<CaseManager>>() {
			@Override
			public void success(ArrayList<CaseManager> result) {
				try {
					setCases(result);
				}catch(Exception e) {
					Logger.remote.log(Level.SEVERE,e.getMessage(),e);
					Window.alert(e.getMessage());
				}
			}
			@Override
			public void failure(Throwable caught) {
				// TODO Auto-generated method stub
				super.failure(caught);
				Window.alert(caught.getMessage());
			}
		});
		ArrayList<DictionaryDO> tags = CategoryCache.getBySystemName("case_tags");
		
		setTags(tags);
		
		setTagsTable(tags);
		
	}
	
	protected void setTags(ArrayList<DictionaryDO> tags) {
		ArrayList<Item<Integer>> model;
		
		model = new ArrayList<Item<Integer>>();
		
		for(DictionaryDO tag : tags) {
			Item<Integer> item = new Item<Integer>(tag.getId(),tag.getEntry());
			model.add(item);
		}
		
		ui.tags().setModel(model);
	}
	
	protected void setTagsTable(ArrayList<DictionaryDO> tags) {
		ArrayList<Row> model;
		
		model = new ArrayList<Row>();
		
		for(DictionaryDO tag : tags) {
			Row row = new Row(1);
			row.setCell(0, tag.getEntry());
			row.setData(tag);
			model.add(row);
		}
		
		ui.tagTable().setModel(model);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				ui.tagTable().onResize();
			}
		});	
	}
	
	protected void setCases(ArrayList<CaseManager> cases) throws Exception {
		ArrayList<Row> model;
		
		model = new ArrayList<Row>();
		
		for(CaseManager _case : cases) {
			Row row = new Row(5);
			row.setCell(0, new CheckLabelValue("N",_case.getPatient().getLastName()+", "+_case.getPatient().getFirstName()));
			row.setCell(1, _case.getPatient().getBirthDate());
			row.setCell(2, _case.getOrganization().getName());
			row.setCell(3, UserCache.getSystemUser(_case.getCaseUser().getSystemUserId()).getLoginName());
			row.setCell(4, createStatus(_case));
			model.add(row);
		}
		
		ui.getCases().setModel(model);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				ui.getCases().onResize();
			}
		});	
	}
	
	
	protected String createStatus(CaseManager _case) {
		StringBuffer status = new StringBuffer();
		DateHelper helper = new DateHelper();
		helper.setBegin(Datetime.YEAR);
		helper.setEnd(Datetime.DAY);
		
		for(int i = 0; i < _case.tag.count(); i++) {
			CaseTagDO tag = _case.tag.get(i);
			String text = "";
			
			try {
				text = DictionaryCache.getById(tag.getTypeId()).getEntry();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			if(i > 0)
				status.append("\n");
			
			status.append(text+("("+helper.format(tag.getCreatedDate())+")"));
		}
		
		return status.toString();
	}

	@Override
	public void setSize(String width, String height) {
		super.setSize(width, height);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				ui.getCases().onResize();
			}
		});
	}
}
