package org.openelis.domain;

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;

public class SampleTrackingVO implements RPC {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer accession;
	protected Integer status;
	
	protected ArrayList<SampleItemVO> items;
	protected ArrayList<SampleQaEventViewDO> qaevents;
	protected ArrayList<IdNameVO> notes;
	protected boolean hasAuxData;

	public SampleTrackingVO() {
		
	}
	
	public SampleTrackingVO(Integer id, Integer accession, Integer status)  {
		this.id = id;
		this.accession = accession;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAccession() {
		return accession;
	}

	public void setAccession(Integer accession) {
		this.accession = accession;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public void setItems(ArrayList<SampleItemVO> items) {
		this.items = items;
	}
	
	public ArrayList<SampleItemVO> getItems() {
		return items;
	}
	
	public ArrayList<SampleQaEventViewDO> getQaEvents() {
		return qaevents;
	}

	public void setQaEvents(ArrayList<SampleQaEventViewDO> qaevents) {
		this.qaevents = qaevents;
	}
	
	public ArrayList<IdNameVO> getNotes() {
		return notes;
	}

	public void setNotes(ArrayList<IdNameVO> notes) {
		this.notes = notes;
	}
	
	public boolean hasAuxData() {
		return hasAuxData;
	}

	public void setHasAuxData(boolean hasAuxData) {
		this.hasAuxData = hasAuxData;
	}

}
