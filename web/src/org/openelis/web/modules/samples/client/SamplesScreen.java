package org.openelis.web.modules.samples.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;

import com.google.gwt.core.client.GWT;

public class SamplesScreen extends Screen {

	public SamplesScreen() {
		super((ScreenDefInt)GWT.create(SamplesDef.class));
	}
}
