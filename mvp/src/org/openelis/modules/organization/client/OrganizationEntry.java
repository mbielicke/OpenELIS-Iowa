package org.openelis.modules.organization.client;

import org.openelis.di.AppDI;

import com.google.gwt.core.client.EntryPoint;

public class OrganizationEntry implements EntryPoint {

	@Override
	public void onModuleLoad() {
		AppDI.INSTANCE.organizationPoint().onModuleLoad();
	}

}
