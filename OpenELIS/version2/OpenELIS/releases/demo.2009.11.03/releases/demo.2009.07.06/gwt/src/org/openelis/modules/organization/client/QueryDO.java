package org.openelis.modules.organization.client;

import org.openelis.gwt.common.RPC;

public class QueryDO implements RPC{
	private static final long serialVersionUID = 1L;
	public Integer id;
	public String name;
	
	public QueryDO(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

}
