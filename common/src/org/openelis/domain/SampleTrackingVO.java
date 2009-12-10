package org.openelis.domain;

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;

public class SampleTrackingVO implements RPC {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer accession;
	
	protected ArrayList<SampleItemVO> items;
	
	public SampleTrackingVO() {
		
	}
	
	public SampleTrackingVO(Integer id, Integer accession)  {
		this.id = id;
		this.accession = accession;
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
	
	public void setItems(ArrayList<SampleItemVO> items) {
		this.items = items;
	}
	
	public ArrayList<SampleItemVO> getItems() {
		return items;
	}

}
