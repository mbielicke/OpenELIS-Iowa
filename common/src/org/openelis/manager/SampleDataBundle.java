/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.manager;

import java.io.Serializable;


public class SampleDataBundle implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		SAMPLE, SAMPLE_ITEM, ANALYSIS
	};

	protected Type type;
	protected int index;
	protected SampleManager sampleManager;
	protected SampleDataBundle parent;

	protected SampleDataBundle() {
	}

	protected SampleDataBundle(Type type, SampleManager sampleManager, SampleDataBundle parent, int index) {
		setType(type);
		setSampleManager(sampleManager);
		setParent(parent);
		setIndex(index);
	}

	public Type getType() {
		return type;
	}

	protected void setType(Type type) {
		this.type = type;
	}

	protected void setIndex(int index) {
		this.index = index;
	}

	public SampleDataBundle getParent() {
		return parent;
	}

	protected void setParent(SampleDataBundle parent) {
		this.parent = parent;
	}

	public SampleManager getSampleManager() {
		return sampleManager;
	}

	protected void setSampleManager(SampleManager sampleManager) {
		this.sampleManager = sampleManager;
	}

	public int getSampleItemIndex() {
		if (type == Type.SAMPLE_ITEM)
			return index;

		if (parent != null)
			return parent.getSampleItemIndex();

		return -1;
	}

	public int getAnalysisIndex() {
		if (type == Type.ANALYSIS)
			return index;

		return -1;
	}
}
