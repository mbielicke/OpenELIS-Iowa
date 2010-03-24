package org.openelis.meta;

public class ReviewReleaseMeta extends SampleMeta {

	@Override
	public String buildFrom(String where) {
		return " Sample _sample, IN (_sample.sampleItem) _sampleItem, IN (_sampleItem.analysis) _analysis LEFT JOIN _analysis.test _test LEFT JOIN _test.method _method";
	}
}
