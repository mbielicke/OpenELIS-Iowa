package org.openelis.domain;

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;

public class SampleItemVO implements RPC {


	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer sequence;
	protected String container;
	protected String source;
	protected String type;
	
	protected ArrayList<AnalysisVO> analysis;

	public SampleItemVO() {
		
	}
	
	public SampleItemVO(Integer id, Integer sequence, String container, String source, String type) {
		this.id = id;
		this.sequence = sequence;
		this.container = container;
		this.source = source;
		this.type = type;
	}
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getContainer() {
		return container;
	}
	public void setContainer(String container) {
		this.container = container;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	public ArrayList<AnalysisVO> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(ArrayList<AnalysisVO> analysis) {
		this.analysis = analysis;
	}
	

}
